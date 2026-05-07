package com.kelvin.lockin.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.kelvin.lockin.ui.theme.InterRegular
import com.kelvin.lockin.ui.theme.OrbitronBold

private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)
private val TextGreen     = Color(0xFF34D399)

@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val scheduleState by viewModel.scheduleState.collectAsState()

    val selectedDays = scheduleState.selectedDays
    val startHour = scheduleState.startHour
    val startMinute = scheduleState.startMinute
    val endHour = scheduleState.endHour
    val endMinute = scheduleState.endMinute
    val isScheduleActive = scheduleState.isScheduleActive

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "SCHEDULE",
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

            // ── Schedule Toggle ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(GlassBorder, Color.White.copy(alpha = 0.03f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Auto Schedule",
                            fontFamily = OrbitronBold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isScheduleActive) "Schedule is active" else "Schedule is off",
                            fontFamily = InterRegular,
                            fontSize = 12.sp,
                            color = if (isScheduleActive) TextGreen else TextMuted
                        )
                    }

                    Switch(
                        checked = isScheduleActive,
                        onCheckedChange = { viewModel.toggleSchedule(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurplePrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = GlassWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Days Selector ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(GlassBorder, Color.White.copy(alpha = 0.03f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Repeat On",
                        fontFamily = OrbitronBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected)
                                            Brush.linearGradient(
                                                listOf(PurplePrimary, PurpleLight)
                                            )
                                        else
                                            SolidColor(GlassWhite)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PurpleLight else GlassBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.toggleDay(day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.first().toString(),
                                    fontFamily = InterRegular,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Time Pickers (12-Hour Format) ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimePickerCard12H(
                    modifier = Modifier.weight(1f),
                    label = "Start Time",
                    hour24 = startHour,
                    minute = startMinute,
                    onHourChange = { viewModel.onStartHourChange(it) },
                    onMinuteChange = { viewModel.onStartMinuteChange(it) }
                )

                TimePickerCard12H(
                    modifier = Modifier.weight(1f),
                    label = "End Time",
                    hour24 = endHour,
                    minute = endMinute,
                    onHourChange = { viewModel.onEndHourChange(it) },
                    onMinuteChange = { viewModel.onEndMinuteChange(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Summary Card ──────────────────────────────────────────────────
            if (selectedDays.isNotEmpty()) {
                val (startH12, startPeriod) = to12Hour(startHour, startMinute)
                val (endH12, endPeriod) = to12Hour(endHour, endMinute)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(PurplePrimary.copy(alpha = 0.15f))
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(PurpleLight.copy(alpha = 0.4f), GlassBorder)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Schedule Summary",
                            fontFamily = OrbitronBold,
                            fontSize = 12.sp,
                            color = PurpleLight,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Every ${selectedDays.joinToString(", ")}",
                            fontFamily = InterRegular,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$startH12 to $endH12",
                            fontFamily = OrbitronBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Save Button ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(PurplePrimary, PurpleLight))
                    )
                    .clickable {
                        viewModel.saveSchedule()
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Save Schedule",
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

// ── 12-Hour Time Picker Card ─────────────────────────────────────────────────
@Composable
private fun TimePickerCard12H(
    modifier: Modifier = Modifier,
    label: String,
    hour24: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val (displayHour, period) = to12Hour(hour24, minute)
    val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12

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

            // Hour (1-12)
            TimeUnit(
                value = hour12,
                range = 1..12,
                onValueChange = { newHour12 ->
                    // Convert 12-hour back to 24-hour
                    val newHour24 = when {
                        newHour12 == 12 && period == "AM" -> 0
                        newHour12 == 12 && period == "PM" -> 12
                        period == "PM" -> newHour12 + 12
                        else -> newHour12
                    }
                    onHourChange(newHour24)
                }
            )

            Text(
                text = ":",
                fontFamily = OrbitronBold,
                fontSize = 24.sp,
                color = TextPrimary
            )

            // Minute (0-59)
            TimeUnit(
                value = minute,
                range = 0..59,
                onValueChange = onMinuteChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            // AM/PM Toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AmPmButton(
                    text = "AM",
                    isSelected = period == "AM",
                    onClick = {
                        if (period == "PM") {
                            val newHour24 = if (hour24 == 12) 0 else hour24 - 12
                            onHourChange(newHour24)
                        }
                    }
                )
                AmPmButton(
                    text = "PM",
                    isSelected = period == "PM",
                    onClick = {
                        if (period == "AM") {
                            val newHour24 = if (hour24 == 0) 12 else hour24 + 12
                            onHourChange(newHour24)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AmPmButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PurplePrimary else GlassWhite)
            .border(
                width = 1.dp,
                color = if (isSelected) PurpleLight else GlassBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = OrbitronBold,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextMuted
        )
    }
}

// ── Time Unit (unchanged logic, just styling) ────────────────────────────────
@Composable
private fun TimeUnit(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(PurplePrimary, PurpleLight))
                )
                .clickable {
                    val next = if (value >= range.last) range.first else value + 1
                    onValueChange(next)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "%02d".format(value),
            fontFamily = OrbitronBold,
            fontSize = 22.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(GlassWhite)
                .border(1.dp, GlassBorder, CircleShape)
                .clickable {
                    val prev = if (value <= range.first) range.last else value - 1
                    onValueChange(prev)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Helper: Convert 24h to 12h format ────────────────────────────────────────
private fun to12Hour(hour24: Int, minute: Int): Pair<String, String> {
    val period = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val timeString = String.format("%02d:%02d %s", hour12, minute, period)
    return Pair(timeString, period)
}