package com.kelvin.lockin.ui.screens.focusmode

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kelvin.lockin.data.repository.BlockedAppsRepository
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.InterRegular
import com.kelvin.lockin.ui.theme.OrbitronBold
import kotlinx.coroutines.flow.first

private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val PurpleDark    = Color(0xFF4C1D95)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)
private val TextGreen     = Color(0xFF34D399)
private val TextOrange    = Color(0xFFFB923C)
private val TextRed       = Color(0xFFEF4444)

@Composable
fun FocusModeScreen(
    navController: NavHostController,
    viewModel: FocusModeViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val focusState by viewModel.focusState.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    val selectedMinutes by viewModel.selectedMinutes.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val preparationSeconds by viewModel.preparationSeconds.collectAsState()
    val sessionStartTime by viewModel.sessionStartTime.collectAsState()
    val sessionEndTime by viewModel.sessionEndTime.collectAsState()
    val savedSchedule by viewModel.savedSchedule.collectAsState()

    val context = LocalContext.current

    // Load blocked app names
    var blockedAppNames by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        val repo = BlockedAppsRepository(context)
        blockedAppNames = repo.blockedAppNames.first()
    }

    // Navigate to Wake Up when finished
    LaunchedEffect(focusState) {
        if (focusState == FocusState.FINISHED) {
            val start = sessionStartTime ?: "--:--"
            val end = sessionEndTime ?: "--:--"
            val duration = "${selectedHours}h ${selectedMinutes}m"

            navController.navigate(
                "${ROUTES.WAKE_UP}?start=$start&end=$end&duration=$duration"
            ) {
                popUpTo(ROUTES.FOCUS_MODE) { inclusive = true }
            }
        }
    }

    // Block back button during focus session
    BackHandler(enabled = focusState == FocusState.RUNNING) {
        // Do nothing — back button is disabled
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

        // Background glow
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

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (focusState != FocusState.RUNNING) {
                            viewModel.endSession()
                            navController.popBackStack()
                        }
                    },
                    enabled = focusState != FocusState.RUNNING
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (focusState == FocusState.RUNNING) TextMuted else TextPrimary
                    )
                }

                Text(
                    text = when (focusState) {
                        FocusState.PREPARING -> "GET READY"
                        FocusState.RUNNING -> "LOCKED IN"
                        else -> "FOCUS MODE"
                    },
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Timer Circle
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(
                        when (focusState) {
                            FocusState.RUNNING -> pulseScale
                            else -> 1f
                        }
                    )
                    .clip(CircleShape)
                    .background(
                        when (focusState) {
                            FocusState.RUNNING -> Brush.radialGradient(
                                listOf(PurplePrimary.copy(alpha = 0.4f), PurpleDark.copy(alpha = 0.2f))
                            )
                            FocusState.PREPARING -> Brush.radialGradient(
                                listOf(TextOrange.copy(alpha = 0.3f), Color.Transparent)
                            )
                            else -> SolidColor(GlassWhite)
                        }
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                when (focusState) {
                                    FocusState.RUNNING -> TextGreen.copy(alpha = 0.8f)
                                    FocusState.PREPARING -> TextOrange.copy(alpha = 0.8f)
                                    else -> GlassBorder
                                },
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
                        FocusState.PREPARING -> {
                            Text(
                                text = "STARTING IN",
                                fontFamily = OrbitronBold,
                                fontSize = 12.sp,
                                color = TextOrange,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$preparationSeconds",
                                fontFamily = OrbitronBold,
                                fontSize = 64.sp,
                                color = TextOrange,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Get ready to focus",
                                fontFamily = InterRegular,
                                fontSize = 12.sp,
                                color = TextMuted
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
                        FocusState.FINISHED -> {
                            Text(
                                text = "COMPLETE",
                                fontFamily = OrbitronBold,
                                fontSize = 14.sp,
                                color = TextGreen,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === PREPARING: Show Scheduled Time + Blocked Apps ===
            if (focusState == FocusState.PREPARING) {

                // Scheduled Time (from Schedule screen)
                if (savedSchedule.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PurplePrimary.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(PurpleLight.copy(alpha = 0.3f), GlassBorder)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SCHEDULED TIME",
                                fontFamily = OrbitronBold,
                                fontSize = 11.sp,
                                color = PurpleLight,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${savedSchedule.startTimeFormatted}  →  ${savedSchedule.endTimeFormatted}",
                                fontFamily = OrbitronBold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Days: ${savedSchedule.days.joinToString(", ")}",
                                fontFamily = InterRegular,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Session Time (this actual session)
                if (sessionStartTime != null && sessionEndTime != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassWhite)
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(listOf(GlassBorder, Color.White.copy(alpha = 0.03f))),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TimeInfo(label = "Session Start", time = sessionStartTime!!)
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(GlassBorder)
                            )
                            TimeInfo(label = "Session End", time = sessionEndTime!!)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Blocked Apps List
                if (blockedAppNames.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassWhite)
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(listOf(GlassBorder, Color.White.copy(alpha = 0.03f))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Blocked",
                                    tint = TextRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${blockedAppNames.size} Apps Will Be Blocked",
                                    fontFamily = OrbitronBold,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.heightIn(max = 120.dp)
                            ) {
                                items(blockedAppNames) { appName ->
                                    Text(
                                        text = "• $appName",
                                        fontFamily = InterRegular,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // === RUNNING: Clean timer + both times ===
            if (focusState == FocusState.RUNNING) {

                // Scheduled time (from Schedule screen)
                if (savedSchedule.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurplePrimary.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(PurpleLight.copy(alpha = 0.2f), GlassBorder)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SCHEDULED",
                                fontFamily = OrbitronBold,
                                fontSize = 10.sp,
                                color = PurpleLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${savedSchedule.startTimeFormatted} → ${savedSchedule.endTimeFormatted}",
                                fontFamily = InterRegular,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Session time (actual)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassWhite)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(GlassBorder, Color.White.copy(alpha = 0.03f))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TimeInfo(label = "Started", time = sessionStartTime ?: "--:--")
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(GlassBorder)
                        )
                        TimeInfo(label = "Ends", time = sessionEndTime ?: "--:--")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Stay focused. Distractions are blocked.",
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Duration Pickers (only in IDLE)
            if (focusState == FocusState.IDLE) {
                Text(
                    text = "Set Duration",
                    fontFamily = OrbitronBold,
                    fontSize = 14.sp,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Main Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (focusState) {
                            FocusState.RUNNING -> Brush.linearGradient(
                                listOf(Color(0xFF1a1a2e), Color(0xFF16213e))
                            )
                            FocusState.PREPARING -> Brush.linearGradient(listOf(TextOrange, Color(0xFFFB923C)))
                            else -> Brush.linearGradient(listOf(PurplePrimary, PurpleLight))
                        }
                    )
                    .clickable(enabled = focusState != FocusState.RUNNING) {
                        when (focusState) {
                            FocusState.PREPARING -> viewModel.endSession()
                            else -> viewModel.startPreparation()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (focusState) {
                        FocusState.RUNNING -> "LOCKED IN — NO EXIT"
                        FocusState.PREPARING -> "Cancel"
                        else -> "Start Session"
                    },
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (focusState == FocusState.RUNNING) TextMuted else Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TimeInfo(label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = InterRegular,
            fontSize = 11.sp,
            color = TextMuted
        )
        Text(
            text = time,
            fontFamily = OrbitronBold,
            fontSize = 16.sp,
            color = TextPrimary
        )
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