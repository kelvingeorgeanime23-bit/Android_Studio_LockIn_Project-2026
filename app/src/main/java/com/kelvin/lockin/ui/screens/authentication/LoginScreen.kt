package com.kelvin.lockin.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kelvin.lockin.R
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.OrbitronBold
import com.kelvin.lockin.ui.theme.InterRegular

// LockIn brand colours
private val BgColor       = Color(0xFF0F0F1A)
private val PurplePrimary = Color(0xFF7C3AED)
private val PurpleLight   = Color(0xFFA855F7)
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(ROUTES.DASHBOARD) {
                    popUpTo(ROUTES.LOGIN) { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        // ── Glow blob top-left (purple primary) ──────────────────────────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-120).dp, y = (-80).dp)
                .blur(120.dp)
                .background(PurplePrimary.copy(alpha = 0.35f), CircleShape)
        )

        // ── Glow blob bottom-right (purple light) ────────────────────────────
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .blur(120.dp)
                .background(PurpleLight.copy(alpha = 0.25f), CircleShape)
        )

        // ── Glow blob center (subtle depth) ──────────────────────────────────
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset(y = (-60).dp)
                .blur(100.dp)
                .background(PurplePrimary.copy(alpha = 0.12f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Lottie animation ──────────────────────────────────────────────
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.login_screen_lottie_animation)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "LOCKIN",
                fontFamily = OrbitronBold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Welcome back. Stay locked in.",
                fontFamily = InterRegular,
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Glassmorphism card ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(GlassWhite)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                GlassBorder,
                                Color.White.copy(alpha = 0.03f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                "Email",
                                fontFamily = InterRegular,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = PurpleLight
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                            focusedContainerColor  = Color.White.copy(alpha = 0.08f),
                            unfocusedBorderColor   = GlassBorder,
                            focusedBorderColor     = PurpleLight,
                            cursorColor            = PurpleLight,
                            focusedTextColor       = TextPrimary,
                            unfocusedTextColor     = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Password",
                                fontFamily = InterRegular,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = PurpleLight
                            )
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null, tint = TextMuted)
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                            focusedContainerColor  = Color.White.copy(alpha = 0.08f),
                            unfocusedBorderColor   = GlassBorder,
                            focusedBorderColor     = PurpleLight,
                            cursorColor            = PurpleLight,
                            focusedTextColor       = TextPrimary,
                            unfocusedTextColor     = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Forgot Password link
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { navController.navigate(ROUTES.FORGOT_PASSWORD) },
                        fontFamily = InterRegular,
                        fontSize = 13.sp,
                        color = PurpleLight,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Login button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (authState is AuthState.Loading)
                                    Brush.linearGradient(
                                        listOf(
                                            PurplePrimary.copy(alpha = 0.5f),
                                            PurpleLight.copy(alpha = 0.5f)
                                        )
                                    )
                                else
                                    Brush.linearGradient(
                                        listOf(PurplePrimary, PurpleLight)
                                    )
                            )
                            .clickable(enabled = authState !is AuthState.Loading) {
                                viewModel.login(email, password)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
                                fontFamily = OrbitronBold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Footer ────────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account?",
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sign Up",
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTES.SIGNUP)
                    },
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleLight
                )
            }
        }
    }
}