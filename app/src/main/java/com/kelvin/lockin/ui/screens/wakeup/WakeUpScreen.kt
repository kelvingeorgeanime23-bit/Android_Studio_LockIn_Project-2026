package com.kelvin.lockin.ui.screens.wakeup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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

private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)
private val TextGreen     = Color(0xFF34D399)

@Composable
fun WakeUpScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        // ── Glow blob top ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .blur(140.dp)
                .background(PurplePrimary.copy(alpha = 0.35f), CircleShape)
        )

        // ── Glow blob bottom ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 80.dp)
                .blur(120.dp)
                .background(PurpleLight.copy(alpha = 0.2f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Trophy Icon ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                PurplePrimary.copy(alpha = 0.5f),
                                PurpleLight.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(PurpleLight.copy(alpha = 0.5f), GlassBorder)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆",
                    fontSize = 52.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Headline ──────────────────────────────────────────────────────
            Text(
                text = "SESSION COMPLETE",
                fontFamily = OrbitronBold,
                fontSize = 22.sp,
                color = TextPrimary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You locked in and got it done.",
                fontFamily = InterRegular,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Stats Card ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassWhite)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(GlassBorder, Color.White.copy(alpha = 0.03f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WakeUpStat(
                        value = "1",
                        label = "Session"
                    )
                    WakeUpStat(
                        value = "✓",
                        label = "Complete",
                        valueColor = TextGreen
                    )
                    WakeUpStat(
                        value = "+1",
                        label = "Streak"
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Exit Button ───────────────────────────────────────────────────
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
                            popUpTo(ROUTES.DASHBOARD) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BACK TO DASHBOARD",
                    fontFamily = OrbitronBold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun WakeUpStat(
    value: String,
    label: String,
    valueColor: Color = Color(0xFFA855F7)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = OrbitronBold,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
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