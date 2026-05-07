package com.kelvin.lockin.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.InterRegular
import com.kelvin.lockin.ui.theme.OrbitronBold

// ── LockIn brand colours ─────────────────────────────────────────────────────
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

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val isLockedIn by viewModel.isLockedIn.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        // ── Glow blob top-right ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-100).dp)
                .blur(120.dp)
                .background(PurplePrimary.copy(alpha = 0.25f), CircleShape)
        )

        // ── Glow blob bottom-left ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(120.dp)
                .background(PurpleLight.copy(alpha = 0.15f), CircleShape)
        )

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back,",
                        fontFamily = InterRegular,
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                    Text(
                        text = userName.ifEmpty { "Warrior" },
                        fontFamily = OrbitronBold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Focus Status Card ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isLockedIn)
                            Brush.linearGradient(
                                listOf(PurpleDark.copy(alpha = 0.6f), PurplePrimary.copy(alpha = 0.4f))
                            )
                        else
                            SolidColor(GlassWhite)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                if (isLockedIn) PurpleLight.copy(alpha = 0.5f) else GlassBorder,
                                Color.White.copy(alpha = 0.03f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isLockedIn) TextGreen else TextOrange)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLockedIn) "LOCKED IN" else "NOT ACTIVE",
                            fontFamily = OrbitronBold,
                            fontSize = 14.sp,
                            color = if (isLockedIn) TextGreen else TextOrange,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isLockedIn)
                            "Stay focused. You're doing great."
                        else
                            "Ready to lock in? Start a session.",
                        fontFamily = InterRegular,
                        fontSize = 14.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    if (isLockedIn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ends at 6:00 AM",
                            fontFamily = OrbitronBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = weeklyStats.weeklyFocusTime,
                    label = "This Week",
                    color = PurpleLight
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = weeklyStats.sessionsCompleted.toString(),
                    label = "Sessions",
                    color = TextGreen
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${weeklyStats.currentStreak}d",
                    label = "Streak",
                    color = TextOrange
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Start / End Focus Session Button ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isLockedIn)
                            Brush.linearGradient(
                                listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                            )
                        else
                            Brush.linearGradient(
                                listOf(PurplePrimary, PurpleLight)
                            )
                    )
                    .clickable {
                        if (isLockedIn) viewModel.endFocusSession()
                        else navController.navigate(ROUTES.FOCUS_MODE)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLockedIn) "End Session" else "Start Focus Session",
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Action Buttons Row 1 ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CalendarToday,
                    label = "Schedule",
                    onClick = { navController.navigate(ROUTES.SCHEDULE) }
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Apps,
                    label = "Apps",
                    onClick = { navController.navigate(ROUTES.APP_SELECTION) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Action Buttons Row 2 ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Dashboard,
                    label = "Stats",
                    onClick = {
                        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    label = "History",
                    onClick = {
                        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Stat Card ────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontFamily = OrbitronBold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontFamily = InterRegular,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

// ── Action Button ────────────────────────────────────────────────────────────
@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(GlassBorder, Color.White.copy(alpha = 0.03f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PurpleLight,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontFamily = InterRegular,
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}