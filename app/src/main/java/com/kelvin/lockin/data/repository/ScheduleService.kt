package com.kelvin.lockin.data.repository

import android.content.Context

class ScheduleService(private val context: Context) {

    private val repository = ScheduleRepository(context)

    val schedule = repository.schedule

    suspend fun saveSchedule(
        days: Set<String>,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isActive: Boolean
    ) {
        repository.saveSchedule(days, startHour, startMinute, endHour, endMinute, isActive)
    }
}