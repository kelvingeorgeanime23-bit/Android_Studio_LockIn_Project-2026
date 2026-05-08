package com.kelvin.lockin.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scheduleDataStore: DataStore<Preferences> by preferencesDataStore(name = "schedule_prefs")

class ScheduleRepository(private val context: Context) {

    private val DAYS_KEY = stringSetPreferencesKey("schedule_days")
    private val START_HOUR_KEY = intPreferencesKey("schedule_start_hour")
    private val START_MIN_KEY = intPreferencesKey("schedule_start_minute")
    private val END_HOUR_KEY = intPreferencesKey("schedule_end_hour")
    private val END_MIN_KEY = intPreferencesKey("schedule_end_minute")
    private val ACTIVE_KEY = booleanPreferencesKey("schedule_active")

    // Read schedule as a data class
    val schedule: Flow<SavedSchedule> = context.scheduleDataStore.data.map { prefs ->
        SavedSchedule(
            days = prefs[DAYS_KEY] ?: emptySet(),
            startHour = prefs[START_HOUR_KEY] ?: 22,
            startMinute = prefs[START_MIN_KEY] ?: 0,
            endHour = prefs[END_HOUR_KEY] ?: 6,
            endMinute = prefs[END_MIN_KEY] ?: 0,
            isActive = prefs[ACTIVE_KEY] ?: false
        )
    }

    suspend fun saveSchedule(
        days: Set<String>,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isActive: Boolean
    ) {
        context.scheduleDataStore.edit { prefs ->
            prefs[DAYS_KEY] = days
            prefs[START_HOUR_KEY] = startHour
            prefs[START_MIN_KEY] = startMinute
            prefs[END_HOUR_KEY] = endHour
            prefs[END_MIN_KEY] = endMinute
            prefs[ACTIVE_KEY] = isActive
        }
    }
}

data class SavedSchedule(
    val days: Set<String> = emptySet(),
    val startHour: Int = 22,
    val startMinute: Int = 0,
    val endHour: Int = 6,
    val endMinute: Int = 0,
    val isActive: Boolean = false
) {
    // Format as "10:00 PM"
    val startTimeFormatted: String
        get() = formatTime12Hour(startHour, startMinute)

    val endTimeFormatted: String
        get() = formatTime12Hour(endHour, endMinute)

    private fun formatTime12Hour(hour: Int, minute: Int): String {
        val period = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format("%02d:%02d %s", hour12, minute, period)
    }
}