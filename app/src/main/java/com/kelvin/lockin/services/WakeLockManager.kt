package com.kelvin.lockin.services

import android.content.Context
import android.os.PowerManager

class WakeLockManager(private val context: Context) {

    private var wakeLock: PowerManager.WakeLock? = null

    fun acquire() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "LockIn::FocusWakeLock"
        )
        wakeLock?.acquire(10*60*60*1000L) // 10 hours max (or use acquire() for indefinite)
    }

    fun release() {
        wakeLock?.let {
            if (it.isHeld) it.release()
            wakeLock = null
        }
    }
}