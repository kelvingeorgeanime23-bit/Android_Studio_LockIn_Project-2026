package com.kelvin.lockin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.kelvin.lockin.ui.navigation.AppNavigation
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.LockInTheme

class MainActivity : ComponentActivity() {

    private var pendingRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if accessibility service sent us here
        intent.getStringExtra("navigate_to")?.let {
            pendingRoute = it
        }

        setContent {
            LockInTheme {
                val navController = rememberNavController()

                // Handle navigation from accessibility service
                LaunchedEffect(pendingRoute) {
                    pendingRoute?.let { route ->
                        if (navController.currentDestination?.route != route) {
                            navController.navigate(route) {
                                popUpTo(ROUTES.DASHBOARD) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        pendingRoute = null
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

        intent.getStringExtra("navigate_to")?.let { route ->
            pendingRoute = route
        }
    }
}