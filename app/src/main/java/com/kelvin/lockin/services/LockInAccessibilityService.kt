package com.kelvin.lockin.services

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.kelvin.lockin.data.repository.BlockedAppsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class LockInAccessibilityService : AccessibilityService() {

    companion object {
        const val ACTION_START_BLOCKING = "com.kelvin.lockin.START_BLOCKING"
        const val ACTION_STOP_BLOCKING = "com.kelvin.lockin.STOP_BLOCKING"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var blockedPackages: Set<String> = emptySet()
    @Volatile
    private var isBlockingActive = false

    private val blockingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            android.util.Log.d("LOCKIN_DEBUG", "Broadcast received: ${intent?.action}")
            when (intent?.action) {
                ACTION_START_BLOCKING -> {
                    isBlockingActive = true
                    loadBlockedApps()
                }
                ACTION_STOP_BLOCKING -> {
                    isBlockingActive = false
                    blockedPackages = emptySet()
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        android.util.Log.d("LOCKIN_DEBUG", "Service connected")
        val filter = IntentFilter().apply {
            addAction(ACTION_START_BLOCKING)
            addAction(ACTION_STOP_BLOCKING)
        }
        registerReceiver(blockingReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    private fun loadBlockedApps() {
        serviceScope.launch {
            val repo = BlockedAppsRepository(applicationContext)
            blockedPackages = repo.blockedPackages.first()
            android.util.Log.d("LOCKIN_DEBUG", "Blocked apps loaded: $blockedPackages")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isBlockingActive) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == "com.kelvin.lockin") return
        if (packageName == "com.android.systemui") return

        if (!blockedPackages.contains(packageName)) return

        android.util.Log.d("LOCKIN_DEBUG", "BLOCKING $packageName")

        Handler(Looper.getMainLooper()).post {
            val intent = Intent(applicationContext, com.kelvin.lockin.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "focus_mode")
            }
            startActivity(intent)
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(blockingReceiver)
        serviceScope.cancel()
    }
}