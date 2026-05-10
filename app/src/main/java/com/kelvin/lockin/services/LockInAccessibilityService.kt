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

    private val handler = Handler(Looper.getMainLooper())

    private val blockingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
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
        isBlockingActive = false
        blockedPackages = emptySet()

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
        }
    }

    private fun bounceToFocusScreen() {
        handler.post {
            val intent = Intent(applicationContext, com.kelvin.lockin.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", "focus_mode")
            }
            startActivity(intent)
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isBlockingActive) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == "com.kelvin.lockin") return
        if (packageName == "com.android.systemui") return
        if (!blockedPackages.contains(packageName)) return

        bounceToFocusScreen()
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(blockingReceiver)
        serviceScope.cancel()
    }
}