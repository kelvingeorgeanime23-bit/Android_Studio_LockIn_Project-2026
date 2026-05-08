package com.kelvin.lockin.ui.screens.focusmode

import android.app.Application
import android.os.PowerManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelvin.lockin.data.repository.ScheduleRepository
import com.kelvin.lockin.data.repository.SavedSchedule
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

    // Wake Lock to keep screen on during focus
    private val powerManager = application.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var wakeLock: PowerManager.WakeLock? = null

    // Read saved schedule
    val savedSchedule: StateFlow<SavedSchedule> = scheduleRepository.schedule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedSchedule())

    private val _focusState = MutableStateFlow(FocusState.IDLE)
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

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

    fun onHoursChanged(hours: Int) {
        if (_focusState.value == FocusState.IDLE) {
            _selectedHours.value = hours
        }
    }

    fun onMinutesChanged(minutes: Int) {
        if (_focusState.value == FocusState.IDLE) {
            _selectedMinutes.value = minutes
        }
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

        // Acquire wake lock to keep screen on
        acquireWakeLock()

        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
            }
            _focusState.value = FocusState.FINISHED
            releaseWakeLock()
        }
    }

    fun endSession() {
        timerJob?.cancel()
        releaseWakeLock()
        _focusState.value = FocusState.IDLE
        _remainingSeconds.value = 0L
        _preparationSeconds.value = 10
        _sessionStartTime.value = null
        _sessionEndTime.value = null
    }

    private fun acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "LockIn::FocusWakeLock"
        )
        wakeLock?.acquire(10 * 60 * 60 * 1000L) // 10 hours max
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
        return if (h > 0) {
            "%02d:%02d:%02d".format(h, m, s)
        } else {
            "%02d:%02d".format(m, s)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        releaseWakeLock()
    }
}