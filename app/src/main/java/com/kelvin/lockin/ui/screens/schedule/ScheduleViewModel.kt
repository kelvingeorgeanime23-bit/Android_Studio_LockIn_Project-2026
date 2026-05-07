package com.kelvin.lockin.ui.screens.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Application.scheduleDataStore: DataStore<Preferences> by preferencesDataStore(name = "schedule_prefs")

data class ScheduleState(
    val selectedDays: Set<String> = emptySet(),
    val startHour: Int = 22,      // 22:00 = 10:00 PM
    val startMinute: Int = 0,
    val endHour: Int = 6,         // 06:00 = 6:00 AM
    val endMinute: Int = 0,
    val isScheduleActive: Boolean = false
)

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.scheduleDataStore

    private val DAYS_KEY = stringSetPreferencesKey("selected_days")
    private val START_HOUR_KEY = androidx.datastore.preferences.core.intPreferencesKey("start_hour")
    private val START_MIN_KEY = androidx.datastore.preferences.core.intPreferencesKey("start_min")
    private val END_HOUR_KEY = androidx.datastore.preferences.core.intPreferencesKey("end_hour")
    private val END_MIN_KEY = androidx.datastore.preferences.core.intPreferencesKey("end_min")
    private val ACTIVE_KEY = booleanPreferencesKey("is_active")

    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                _scheduleState.value = ScheduleState(
                    selectedDays = prefs[DAYS_KEY] ?: emptySet(),
                    startHour = prefs[START_HOUR_KEY] ?: 22,
                    startMinute = prefs[START_MIN_KEY] ?: 0,
                    endHour = prefs[END_HOUR_KEY] ?: 6,
                    endMinute = prefs[END_MIN_KEY] ?: 0,
                    isScheduleActive = prefs[ACTIVE_KEY] ?: false
                )
            }
        }
    }

    fun toggleDay(day: String) {
        val current = _scheduleState.value.selectedDays.toMutableSet()
        if (current.contains(day)) current.remove(day) else current.add(day)
        _scheduleState.value = _scheduleState.value.copy(selectedDays = current)
    }

    fun onStartHourChange(hour: Int) {
        _scheduleState.value = _scheduleState.value.copy(startHour = hour)
    }

    fun onStartMinuteChange(minute: Int) {
        _scheduleState.value = _scheduleState.value.copy(startMinute = minute)
    }

    fun onEndHourChange(hour: Int) {
        _scheduleState.value = _scheduleState.value.copy(endHour = hour)
    }

    fun onEndMinuteChange(minute: Int) {
        _scheduleState.value = _scheduleState.value.copy(endMinute = minute)
    }

    fun toggleSchedule(active: Boolean) {
        _scheduleState.value = _scheduleState.value.copy(isScheduleActive = active)
    }

    fun saveSchedule() {
        viewModelScope.launch {
            val state = _scheduleState.value
            dataStore.edit { prefs ->
                prefs[DAYS_KEY] = state.selectedDays
                prefs[START_HOUR_KEY] = state.startHour
                prefs[START_MIN_KEY] = state.startMinute
                prefs[END_HOUR_KEY] = state.endHour
                prefs[END_MIN_KEY] = state.endMinute
                prefs[ACTIVE_KEY] = state.isScheduleActive
            }
        }
    }
}