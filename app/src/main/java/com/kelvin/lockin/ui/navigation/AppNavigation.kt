package com.kelvin.lockin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelvin.lockin.ui.screens.appselection.AppSelectionScreen
import com.kelvin.lockin.ui.screens.authentication.ForgotPasswordScreen
import com.kelvin.lockin.ui.screens.authentication.LoginScreen
import com.kelvin.lockin.ui.screens.authentication.SignUpScreen
import com.kelvin.lockin.ui.screens.dashboard.DashboardScreen
import com.kelvin.lockin.ui.screens.focusmode.FocusModeScreen
import com.kelvin.lockin.ui.screens.onboarding.OnboardingScreen
import com.kelvin.lockin.ui.screens.schedule.ScheduleScreen
import com.kelvin.lockin.ui.screens.splash.SplashScreen
import com.kelvin.lockin.ui.screens.wakeup.WakeUpScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTES.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTES.SPLASH) { SplashScreen(navController = navController) }
        composable(ROUTES.ONBOARDING) { OnboardingScreen(navController = navController) }
        composable(ROUTES.LOGIN) { LoginScreen(navController = navController) }
        composable(ROUTES.SIGNUP) { SignUpScreen(navController = navController) }
        composable(ROUTES.FORGOT_PASSWORD) { ForgotPasswordScreen(navController = navController) }
        composable(ROUTES.DASHBOARD) { DashboardScreen(navController = navController) }
        composable(ROUTES.SCHEDULE) { ScheduleScreen(navController = navController) }
        composable(ROUTES.APP_SELECTION) { AppSelectionScreen(navController = navController) }
        composable(ROUTES.FOCUS_MODE) { FocusModeScreen(navController = navController) }
        composable(ROUTES.WAKE_UP) { WakeUpScreen(navController = navController) }
    }
}