package com.kelvin.lockin

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.kelvin.lockin.ui.navigation.AppNavigation
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.LockInTheme

class MainActivity : ComponentActivity() {

    private var pendingRoute by mutableStateOf<String?>(null)
    private var pendingStartTime by mutableStateOf<String?>(null)
    private var pendingEndTime by mutableStateOf<String?>(null)
    private var pendingRemaining by mutableLongStateOf(-1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            LockInTheme {
                val navController = rememberNavController()
                val app = applicationContext as LockInApp

                LaunchedEffect(pendingRoute) {
                    pendingRoute?.let { route ->
                        if (pendingRemaining > 0 &&
                            pendingStartTime != null &&
                            pendingEndTime != null) {
                            app.focusModeViewModel.restoreFromAlarm(
                                startTime = pendingStartTime!!,
                                endTime = pendingEndTime!!,
                                remainingSeconds = pendingRemaining
                            )
                        }
                        if (navController.currentDestination?.route != route) {
                            navController.navigate(route) {
                                popUpTo(ROUTES.DASHBOARD) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        pendingRoute = null
                        pendingStartTime = null
                        pendingEndTime = null
                        pendingRemaining = -1L
                    }
                }

                AppNavigation(
                    navController = navController,
                    startDestination = ROUTES.SPLASH
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra("navigate_to")?.let {
            pendingRoute = it
            pendingStartTime = intent.getStringExtra("session_start_time")
            pendingEndTime = intent.getStringExtra("session_end_time")
            pendingRemaining = intent.getLongExtra("session_remaining", -1L)
            killPictureInPicture()
        }
    }

    private fun killPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.appTasks.forEach { task ->
                task.taskInfo?.topActivity?.packageName?.let { pkg ->
                    if (pkg != packageName) {
                        task.finishAndRemoveTask()
                    }
                }
            }
        }
    }
}