package com.kelvin.lockin.ui.screens.focusmode

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.InterRegular
import com.kelvin.lockin.ui.theme.OrbitronBold

private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val PurpleDark    = Color(0xFF4C1D95)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)
private val TextGreen     = Color(0xFF34D399)

@Composable
fun FocusModeScreen(
    navController: NavHostController,
    viewModel: FocusModeViewModel = viewModel()
) {
    val focusState by viewModel.focusState.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    val selectedMinutes by viewModel.selectedMinutes.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    LaunchedEffect(focusState) {
        if (focusState == FocusState.FINISHED) {
            navController.navigate(ROUTES.WAKE_UP) {
                popUpTo(ROUTES.FOCUS_MODE) { inclusive = true }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .blur(120.dp)
                .background(PurplePrimary.copy(alpha = 0.3f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.endSession()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "FOCUS MODE",
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(if (focusState == FocusState.RUNNING) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(
                        if (focusState == FocusState.RUNNING)
                            Brush.radialGradient(
                                listOf(
                                    PurplePrimary.copy(alpha = 0.4f),
                                    PurpleDark.copy(alpha = 0.2f)
                                )
                            )
                        else
                            SolidColor(GlassWhite)
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                if (focusState == FocusState.RUNNING)
                                    PurpleLight.copy(alpha = 0.8f)
                                else
                                    GlassBorder,
                                Color.White.copy(alpha = 0.03f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (focusState) {
                        FocusState.IDLE -> {
                            Text(
                                text = "SET TIME",
                                fontFamily = OrbitronBold,
                                fontSize = 14.sp,
                                color = TextMuted,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "%02d:%02d".format(selectedHours, selectedMinutes),
                                fontFamily = OrbitronBold,
                                fontSize = 48.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        FocusState.RUNNING -> {
                            Text(
                                text = "LOCKED IN",
                                fontFamily = OrbitronBold,
                                fontSize = 12.sp,
                                color = TextGreen,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.formatTime(remainingSeconds),
                                fontFamily = OrbitronBold,
                                fontSize = 48.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "remaining",
                                fontFamily = InterRegular,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        FocusState.FINISHED -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (focusState == FocusState.IDLE) {
                Text(
                    text = "Set Duration",
                    fontFamily = OrbitronBold,
                    fontSize = 14.sp,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DurationPicker(
                        modifier = Modifier.weight(1f),
                        label = "Hours",
                        value = selectedHours,
                        range = 0..23,
                        onValueChange = { viewModel.onHoursChanged(it) }
                    )

                    DurationPicker(
                        modifier = Modifier.weight(1f),
                        label = "Minutes",
                        value = selectedMinutes,
                        range = 0..59,
                        onValueChange = { viewModel.onMinutesChanged(it) }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (focusState == FocusState.RUNNING) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Stay focused. You've got this.",
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (focusState == FocusState.RUNNING)
                            Brush.linearGradient(
                                listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                            )
                        else
                            Brush.linearGradient(
                                listOf(PurplePrimary, PurpleLight)
                            )
                    )
                    .clickable {
                        if (focusState == FocusState.RUNNING) {
                            viewModel.endSession()
                            navController.popBackStack()
                        } else {
                            viewModel.startSession()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (focusState == FocusState.RUNNING) "End Session" else "Start Session",
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DurationPicker(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(GlassBorder, Color.White.copy(alpha = 0.03f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontFamily = InterRegular,
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable {
                            if (value > range.first) onValueChange(value - 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "%02d".format(value),
                    fontFamily = OrbitronBold,
                    fontSize = 28.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PurplePrimary, PurpleLight))
                        )
                        .clickable {
                            if (value < range.last) onValueChange(value + 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}