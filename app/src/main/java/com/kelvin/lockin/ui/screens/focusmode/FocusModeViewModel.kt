package com.kelvin.lockin.ui.screens.focusmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FocusState {
    IDLE,
    RUNNING,
    FINISHED
}

class FocusModeViewModel : ViewModel() {

    private val _focusState = MutableStateFlow(FocusState.IDLE)
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

    private val _selectedHours = MutableStateFlow(0)
    val selectedHours: StateFlow<Int> = _selectedHours.asStateFlow()

    private val _selectedMinutes = MutableStateFlow(25)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun onHoursChanged(hours: Int) {
        _selectedHours.value = hours
    }

    fun onMinutesChanged(minutes: Int) {
        _selectedMinutes.value = minutes
    }

    fun startSession() {
        val totalSeconds = (_selectedHours.value * 3600 + _selectedMinutes.value * 60).toLong()
        if (totalSeconds == 0L) return

        _remainingSeconds.value = totalSeconds
        _focusState.value = FocusState.RUNNING

        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
            }
            _focusState.value = FocusState.FINISHED
        }
    }

    fun endSession() {
        timerJob?.cancel()
        _focusState.value = FocusState.IDLE
        _remainingSeconds.value = 0L
        _selectedHours.value = 0
        _selectedMinutes.value = 25
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
}