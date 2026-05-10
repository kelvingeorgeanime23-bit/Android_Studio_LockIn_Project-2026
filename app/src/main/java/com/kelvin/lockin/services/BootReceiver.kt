package com.kelvin.lockin.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kelvin.lockin.data.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val repo = ScheduleRepository(context)
                val schedule = repo.schedule.first()
                if (schedule.isActive) {
                    ScheduleAlarmReceiver.scheduleAlarms(context, schedule)
                }
            }
        }
    }
}