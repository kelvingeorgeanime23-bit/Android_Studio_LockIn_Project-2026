package com.kelvin.lockin.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kelvin.lockin.data.repository.ScheduleRepository
import com.kelvin.lockin.data.repository.SavedSchedule
import com.kelvin.lockin.data.repository.sessionDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ScheduleAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SCHEDULE_START = "com.kelvin.lockin.SCHEDULE_START"
        const val ACTION_SCHEDULE_END = "com.kelvin.lockin.SCHEDULE_END"
        const val EXTRA_DURATION_MINUTES = "duration_minutes"
        private const val TAG = "LOCKIN_SCHEDULE"
        private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        fun scheduleAlarms(context: Context, schedule: SavedSchedule) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            cancelAlarms(context)

            if (!schedule.isActive || schedule.days.isEmpty()) return

            val now = Calendar.getInstance()
            val currentDayIndex = (now.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            var daysUntilNext = -1
            for (i in 0..6) {
                val checkIndex = (currentDayIndex + i) % 7
                if (schedule.days.contains(dayNames[checkIndex])) {
                    daysUntilNext = i
                    break
                }
            }

            if (daysUntilNext == -1) return

            val startCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, daysUntilNext)
                set(Calendar.HOUR_OF_DAY, schedule.startHour)
                set(Calendar.MINUTE, schedule.startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (daysUntilNext == 0 && startCal.before(Calendar.getInstance())) {
                for (i in 1..7) {
                    val checkIndex = (currentDayIndex + i) % 7
                    if (schedule.days.contains(dayNames[checkIndex])) {
                        startCal.add(Calendar.DAY_OF_YEAR, i)
                        break
                    }
                }
            }

            // Calculate duration directly from schedule times
            var durationMinutes = (schedule.endHour * 60 + schedule.endMinute) -
                    (schedule.startHour * 60 + schedule.startMinute)

            // If end time is before start time, session crosses midnight
            if (durationMinutes <= 0) durationMinutes += 24 * 60

            // Set end alarm by adding duration to startCal, not by setting hour/minute independently
            val endCal = startCal.clone() as Calendar
            endCal.add(Calendar.MINUTE, durationMinutes)

            val startIntent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
                action = ACTION_SCHEDULE_START
                putExtra(EXTRA_DURATION_MINUTES, durationMinutes)
            }
            val startPending = PendingIntent.getBroadcast(
                context, 0, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val endIntent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
                action = ACTION_SCHEDULE_END
            }
            val endPending = PendingIntent.getBroadcast(
                context, 1, endIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startCal.timeInMillis, startPending)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endCal.timeInMillis, endPending)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startCal.timeInMillis, startPending)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endCal.timeInMillis, endPending)
            }

            Log.d(TAG, "Alarms set: start=${startCal.time}, end=${endCal.time}, duration=$durationMinutes mins")
        }

        fun cancelAlarms(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val startPending = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, ScheduleAlarmReceiver::class.java).apply { action = ACTION_SCHEDULE_START },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val endPending = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, ScheduleAlarmReceiver::class.java).apply { action = ACTION_SCHEDULE_END },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(startPending)
            alarmManager.cancel(endPending)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SCHEDULE_START -> {
                val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 60)

                CoroutineScope(Dispatchers.IO).launch {
                    val now = LocalTime.now()
                    val endTime = now.plusMinutes(durationMinutes.toLong())
                    val startTimeStr = now.format(timeFormatter)
                    val endTimeStr = endTime.format(timeFormatter)
                    val remainingSeconds = durationMinutes * 60L

                    context.sessionDataStore.edit { prefs ->
                        prefs[booleanPreferencesKey("is_running")] = true
                        prefs[longPreferencesKey("remaining_seconds")] = remainingSeconds
                        prefs[stringPreferencesKey("start_time")] = startTimeStr
                        prefs[stringPreferencesKey("end_time")] = endTimeStr
                    }

                    val startBlockIntent = Intent(LockInAccessibilityService.ACTION_START_BLOCKING).apply {
                        setPackage(context.packageName)
                    }
                    context.sendBroadcast(startBlockIntent)

                    val bounceIntent = Intent(context, com.kelvin.lockin.MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("navigate_to", "focus_mode")
                        putExtra("session_start_time", startTimeStr)
                        putExtra("session_end_time", endTimeStr)
                        putExtra("session_remaining", remainingSeconds)
                    }
                    context.startActivity(bounceIntent)
                }

                showNotification(context, "Focus Session Started", "Scheduled blocking is now active")

                CoroutineScope(Dispatchers.IO).launch {
                    val repo = ScheduleRepository(context)
                    val schedule = repo.schedule.first()
                    if (schedule.isActive) scheduleAlarms(context, schedule)
                }
            }
            ACTION_SCHEDULE_END -> {
                CoroutineScope(Dispatchers.IO).launch {
                    // Clear DataStore first
                    context.sessionDataStore.edit { it.clear() }

                    // Then stop blocking
                    val stopBlockIntent = Intent(LockInAccessibilityService.ACTION_STOP_BLOCKING).apply {
                        setPackage(context.packageName)
                    }
                    context.sendBroadcast(stopBlockIntent)

                    // Then open wake up screen
                    val wakeUpIntent = Intent(context, com.kelvin.lockin.MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("navigate_to", "wake_up")
                    }
                    context.startActivity(wakeUpIntent)
                }

                showNotification(context, "Focus Session Ended", "Apps are unblocked. Great work!")
            }
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("lockin_schedule", "LockIn Schedule", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "lockin_schedule")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}