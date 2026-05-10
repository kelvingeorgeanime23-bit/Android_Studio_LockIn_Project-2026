package com.kelvin.lockin.ui.screens.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelvin.lockin.data.repository.ScheduleRepository
import com.kelvin.lockin.data.repository.SavedSchedule
import com.kelvin.lockin.services.ScheduleAlarmReceiver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ScheduleState(
    val selectedDays: Set<String> = emptySet(),
    val startHour: Int = 22,
    val startMinute: Int = 0,
    val endHour: Int = 6,
    val endMinute: Int = 0,
    val isScheduleActive: Boolean = false
)

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.schedule.collect { saved ->
                _scheduleState.value = ScheduleState(
                    selectedDays = saved.days,
                    startHour = saved.startHour,
                    startMinute = saved.startMinute,
                    endHour = saved.endHour,
                    endMinute = saved.endMinute,
                    isScheduleActive = saved.isActive
                )
            }
        }
    }

    fun toggleDay(day: String) {
        val current = _scheduleState.value.selectedDays.toMutableSet()
        if (current.contains(day)) current.remove(day) else current.add(day)
        _scheduleState.value = _scheduleState.value.copy(selectedDays = current)
    }

    fun onStartHourChange(hour: Int) { _scheduleState.value = _scheduleState.value.copy(startHour = hour) }
    fun onStartMinuteChange(minute: Int) { _scheduleState.value = _scheduleState.value.copy(startMinute = minute) }
    fun onEndHourChange(hour: Int) { _scheduleState.value = _scheduleState.value.copy(endHour = hour) }
    fun onEndMinuteChange(minute: Int) { _scheduleState.value = _scheduleState.value.copy(endMinute = minute) }
    fun toggleSchedule(active: Boolean) { _scheduleState.value = _scheduleState.value.copy(isScheduleActive = active) }

    fun saveSchedule() {
        viewModelScope.launch {
            val state = _scheduleState.value
            repository.saveSchedule(
                days = state.selectedDays,
                startHour = state.startHour,
                startMinute = state.startMinute,
                endHour = state.endHour,
                endMinute = state.endMinute,
                isActive = state.isScheduleActive
            )

            val savedSchedule = SavedSchedule(
                days = state.selectedDays,
                startHour = state.startHour,
                startMinute = state.startMinute,
                endHour = state.endHour,
                endMinute = state.endMinute,
                isActive = state.isScheduleActive
            )

            if (state.isScheduleActive) {
                ScheduleAlarmReceiver.scheduleAlarms(getApplication(), savedSchedule)
            } else {
                ScheduleAlarmReceiver.cancelAlarms(getApplication())
            }
        }
    }
}