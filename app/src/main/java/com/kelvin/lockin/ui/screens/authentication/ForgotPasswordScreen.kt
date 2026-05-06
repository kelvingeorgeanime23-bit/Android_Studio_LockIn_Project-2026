package com.kelvin.lockin.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
private val GlassWhite    = Color.White.copy(alpha = 0.07f)
private val GlassBorder   = Color.White.copy(alpha = 0.15f)
private val TextPrimary   = Color(0xFFF1F0FF)
private val TextMuted     = Color(0xFF9B8EC4)

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // ── Validation ───────────────────────────────────────────────────────────
    val isEmailValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // ── Observe auth state for password reset ────────────────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    "Reset link sent! Check your email.",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetState()
                // Optional: navigate back to login after delay
                // navController.popBackStack()
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
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Back button ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text = "FORGOT\nPASSWORD?",
                fontFamily = OrbitronBold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No worries. Enter your email and we'll send you a reset link.",
                fontFamily = InterRegular,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

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

                    // ── Email field ───────────────────────────────────────────
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
                        isError = email.isNotBlank() && !isEmailValid,
                        supportingText = {
                            if (email.isNotBlank() && !isEmailValid) {
                                Text(
                                    "Invalid email format",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (isEmailValid) viewModel.forgotPassword(email)
                            }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                            focusedContainerColor  = Color.White.copy(alpha = 0.08f),
                            unfocusedBorderColor   = GlassBorder,
                            focusedBorderColor     = PurpleLight,
                            cursorColor            = PurpleLight,
                            focusedTextColor       = TextPrimary,
                            unfocusedTextColor     = TextPrimary,
                            errorBorderColor       = Color(0xFFCF6679)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Send Reset Link button ────────────────────────────────
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.forgotPassword(email)
                        },
                        enabled = isEmailValid && authState !is AuthState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (authState is AuthState.Loading || !isEmailValid)
                                        Brush.linearGradient(
                                            listOf(
                                                PurplePrimary.copy(alpha = 0.4f),
                                                PurpleLight.copy(alpha = 0.4f)
                                            )
                                        )
                                    else
                                        Brush.linearGradient(
                                            listOf(PurplePrimary, PurpleLight)
                                        )
                                ),
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
                                    text = "Send Reset Link",
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Back to login ─────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Remember your password?",
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Login",
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTES.LOGIN) {
                            popUpTo(ROUTES.FORGOT_PASSWORD) { inclusive = true }
                        }
                    },
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleLight
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
