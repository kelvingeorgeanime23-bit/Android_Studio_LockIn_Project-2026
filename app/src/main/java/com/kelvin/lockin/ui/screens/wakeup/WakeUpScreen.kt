package com.kelvin.lockin.ui.screens.wakeup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.InterRegular
import com.kelvin.lockin.ui.theme.OrbitronBold
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)
private val TextGreen     = Color(0xFF34D399)

@Composable
fun WakeUpScreen(
    navController: NavHostController,
    sessionStartTime: String? = null,
    sessionEndTime: String? = null,
    sessionDuration: String? = null
) {
    val currentTime = remember { LocalTime.now() }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val greeting = when (currentTime.hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else -> "Good Night"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        listOf(PurplePrimary.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Greeting
            Text(
                text = greeting,
                fontFamily = InterRegular,
                fontSize = 18.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time
            Text(
                text = currentTime.format(timeFormatter),
                fontFamily = OrbitronBold,
                fontSize = 64.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Session complete message with actual times
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PurplePrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 32.dp, vertical = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Session Complete",
                        fontFamily = OrbitronBold,
                        fontSize = 20.sp,
                        color = TextGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Show session times if available
                    if (sessionStartTime != null && sessionEndTime != null) {
                        Text(
                            text = "You locked in from",
                            fontFamily = InterRegular,
                            fontSize = 14.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$sessionStartTime → $sessionEndTime",
                            fontFamily = OrbitronBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )

                        if (sessionDuration != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "($sessionDuration)",
                                fontFamily = InterRegular,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    } else {
                        Text(
                            text = "You stayed locked in and crushed it.",
                            fontFamily = InterRegular,
                            fontSize = 14.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Continue to dashboard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(PurplePrimary, PurpleLight))
                    )
                    .clickable {
                        navController.navigate(ROUTES.DASHBOARD) {
                            popUpTo(ROUTES.WAKE_UP) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GO TO DASHBOARD",
                        fontFamily = OrbitronBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}