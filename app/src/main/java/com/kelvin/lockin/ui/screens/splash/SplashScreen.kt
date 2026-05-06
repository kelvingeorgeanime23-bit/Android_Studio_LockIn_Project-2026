package com.kelvin.lockin.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kelvin.lockin.R
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.BackgroundDark
import com.kelvin.lockin.ui.theme.OrbitronFont
import com.kelvin.lockin.ui.theme.PurpleLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // FADE IN ANIMATION STATE
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_fade"
    )

    // LOTTIE ANIMATION
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lockin_splash_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // NAVIGATION STATE
    var canNavigate by remember { mutableStateOf(false) }

    // LAUNCH EFFECT: fade in then navigate
    LaunchedEffect(Unit) {
        visible = true
        delay(3000L)
        canNavigate = true
    }

    // NAVIGATE WHEN READY (composition loaded + minimum time passed)
    LaunchedEffect(canNavigate, composition) {
        if (canNavigate && composition != null) {
            navController.navigate(ROUTES.ONBOARDING) {
                popUpTo(ROUTES.SPLASH) { inclusive = true }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOTTIE ANIMATION
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // APP NAME
            Text(
                text = "LOCK IN",
                fontFamily = OrbitronFont,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = PurpleLight,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TAGLINE
            Text(
                text = "Focus. Discipline. Control.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}