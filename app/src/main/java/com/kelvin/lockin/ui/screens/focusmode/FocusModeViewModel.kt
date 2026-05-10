package com.kelvin.lockin.ui.screens.focusmode

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelvin.lockin.data.repository.ScheduleRepository
import com.kelvin.lockin.data.repository.SavedSchedule
import com.kelvin.lockin.data.repository.sessionDataStore
import com.kelvin.lockin.services.LockInAccessibilityService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class FocusState {
    IDLE,
    PREPARING,
    RUNNING,
    FINISHED
}

class FocusModeViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = ScheduleRepository(application)
    private val appContext = application.applicationContext
    private val dataStore = application.sessionDataStore

    private val powerManager = application.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var wakeLock: PowerManager.WakeLock? = null

    private val KEY_IS_RUNNING = booleanPreferencesKey("is_running")
    private val KEY_REMAINING = longPreferencesKey("remaining_seconds")
    private val KEY_START_TIME = stringPreferencesKey("start_time")
    private val KEY_END_TIME = stringPreferencesKey("end_time")

    val savedSchedule: StateFlow<SavedSchedule> = scheduleRepository.schedule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedSchedule())

    private val _focusState = MutableStateFlow(FocusState.IDLE)
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

    private val _isRestoring = MutableStateFlow(true)
    val isRestoring: StateFlow<Boolean> = _isRestoring.asStateFlow()

    private val _selectedHours = MutableStateFlow(0)
    val selectedHours: StateFlow<Int> = _selectedHours.asStateFlow()

    private val _selectedMinutes = MutableStateFlow(25)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _preparationSeconds = MutableStateFlow(10)
    val preparationSeconds: StateFlow<Int> = _preparationSeconds.asStateFlow()

    private val _sessionStartTime = MutableStateFlow<String?>(null)
    val sessionStartTime: StateFlow<String?> = _sessionStartTime.asStateFlow()

    private val _sessionEndTime = MutableStateFlow<String?>(null)
    val sessionEndTime: StateFlow<String?> = _sessionEndTime.asStateFlow()

    private var timerJob: Job? = null
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val isRunning = prefs[KEY_IS_RUNNING] ?: false
            val remaining = prefs[KEY_REMAINING] ?: 0L
            val startTime = prefs[KEY_START_TIME]
            val endTime = prefs[KEY_END_TIME]

            if (!isRunning || remaining <= 0 || endTime == null) {
                dataStore.edit { it.clear() }
                _isRestoring.value = false
                return@launch
            }

            try {
                val endLocalTime = LocalTime.parse(endTime, timeFormatter)
                val now = LocalTime.now()
                val sessionExpired = now.isAfter(endLocalTime)

                if (sessionExpired) {
                    dataStore.edit { it.clear() }
                    _isRestoring.value = false
                    return@launch
                }

                val secondsUntilEnd = java.time.Duration.between(now, endLocalTime).seconds

                if (secondsUntilEnd > 0) {
                    _sessionStartTime.value = startTime
                    _sessionEndTime.value = endTime
                    _remainingSeconds.value = secondsUntilEnd
                    _focusState.value = FocusState.RUNNING
                    acquireWakeLock()
                    sendBlockingBroadcast(true)
                    resumeTimer()
                } else {
                    dataStore.edit { it.clear() }
                }

            } catch (e: Exception) {
                dataStore.edit { it.clear() }
            }

            _isRestoring.value = false
        }
    }

    fun onHoursChanged(hours: Int) {
        if (_focusState.value == FocusState.IDLE) _selectedHours.value = hours
    }

    fun onMinutesChanged(minutes: Int) {
        if (_focusState.value == FocusState.IDLE) _selectedMinutes.value = minutes
    }

    fun startPreparation() {
        val totalSeconds = (_selectedHours.value * 3600 + _selectedMinutes.value * 60).toLong()
        if (totalSeconds == 0L) return

        _focusState.value = FocusState.PREPARING
        _preparationSeconds.value = 10

        timerJob = viewModelScope.launch {
            while (_preparationSeconds.value > 0) {
                delay(1000L)
                _preparationSeconds.value -= 1
            }
            startFocusSession(totalSeconds)
        }
    }

    private fun startFocusSession(totalSeconds: Long) {
        val now = LocalTime.now()
        val endTime = now.plusSeconds(totalSeconds)

        _sessionStartTime.value = now.format(timeFormatter)
        _sessionEndTime.value = endTime.format(timeFormatter)
        _remainingSeconds.value = totalSeconds
        _focusState.value = FocusState.RUNNING

        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_IS_RUNNING] = true
                prefs[KEY_REMAINING] = totalSeconds
                prefs[KEY_START_TIME] = now.format(timeFormatter)
                prefs[KEY_END_TIME] = endTime.format(timeFormatter)
            }
        }

        acquireWakeLock()
        sendBlockingBroadcast(true)
        resumeTimer()
    }

    private fun resumeTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1

                if (_remainingSeconds.value % 5 == 0L) {
                    dataStore.edit { prefs ->
                        prefs[KEY_REMAINING] = _remainingSeconds.value
                    }
                }
            }
            _focusState.value = FocusState.FINISHED
            releaseWakeLock()
            sendBlockingBroadcast(false)
            dataStore.edit { it.clear() }
        }
    }

    fun resetSession() {
        _focusState.value = FocusState.IDLE
        _remainingSeconds.value = 0L
        _preparationSeconds.value = 10
        _sessionStartTime.value = null
        _sessionEndTime.value = null
    }

    fun endSession() {
        timerJob?.cancel()
        releaseWakeLock()
        sendBlockingBroadcast(false)
        _focusState.value = FocusState.IDLE
        _remainingSeconds.value = 0L
        _preparationSeconds.value = 10
        _sessionStartTime.value = null
        _sessionEndTime.value = null
        viewModelScope.launch { dataStore.edit { it.clear() } }
    }

    private fun sendBlockingBroadcast(start: Boolean) {
        val action = if (start)
            LockInAccessibilityService.ACTION_START_BLOCKING
        else
            LockInAccessibilityService.ACTION_STOP_BLOCKING

        val intent = Intent(action).apply {
            setPackage(appContext.packageName)
        }
        appContext.sendBroadcast(intent)
    }

    private fun acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "LockIn::FocusWakeLock"
        )
        wakeLock?.acquire(10 * 60 * 60 * 1000L)
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
            wakeLock = null
        }
    }

    fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%02d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        releaseWakeLock()
    }
}