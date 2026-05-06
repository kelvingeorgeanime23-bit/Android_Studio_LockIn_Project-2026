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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // ── Validation ───────────────────────────────────────────────────────────
    val isNameValid = fullName.length >= 2
    val isEmailValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPhoneValid = phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() || it == '+' || it == ' ' }
    val isPasswordValid = password.length >= 6
    val isConfirmPasswordValid = confirmPassword == password && confirmPassword.isNotBlank()
    val isFormValid = isNameValid && isEmailValid && isPhoneValid && isPasswordValid && isConfirmPasswordValid && termsAccepted

    // ── Observe auth state ───────────────────────────────────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    "Account created! Welcome to LockIn.",
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate(ROUTES.DASHBOARD) {
                    popUpTo(ROUTES.SIGNUP) { inclusive = true }
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
                .verticalScroll(rememberScrollState())
                .imePadding()
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
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text = "JOIN LOCKIN",
                fontFamily = OrbitronBold,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create your account. Start locking in.",
                fontFamily = InterRegular,
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

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

                    // ── Full Name field ───────────────────────────────────────
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = {
                            Text(
                                "Full Name",
                                fontFamily = InterRegular,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = PurpleLight
                            )
                        },
                        isError = fullName.isNotBlank() && !isNameValid,
                        supportingText = {
                            if (fullName.isNotBlank() && !isNameValid) {
                                Text(
                                    "Name must be at least 2 characters",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
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

                    Spacer(modifier = Modifier.height(12.dp))

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
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Phone Number field ────────────────────────────────────
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            // Only allow digits, +, and spaces
                            val filtered = it.filter { char -> char.isDigit() || char == '+' || char == ' ' }
                            phoneNumber = filtered
                        },
                        label = {
                            Text(
                                "Phone Number",
                                fontFamily = InterRegular,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = PurpleLight
                            )
                        },
                        placeholder = {
                            Text(
                                "+254 712 345 678",
                                fontFamily = InterRegular,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        },
                        isError = phoneNumber.isNotBlank() && !isPhoneValid,
                        supportingText = {
                            if (phoneNumber.isNotBlank() && !isPhoneValid) {
                                Text(
                                    "Enter a valid phone number",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Password field ────────────────────────────────────────
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
                        isError = password.isNotBlank() && !isPasswordValid,
                        supportingText = {
                            if (password.isNotBlank() && !isPasswordValid) {
                                Text(
                                    "Password must be at least 6 characters",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Confirm Password field ────────────────────────────────
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = {
                            Text(
                                "Confirm Password",
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
                            val icon = if (confirmPasswordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(icon, contentDescription = null, tint = TextMuted)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        isError = confirmPassword.isNotBlank() && !isConfirmPasswordValid,
                        supportingText = {
                            if (confirmPassword.isNotBlank() && !isConfirmPasswordValid) {
                                Text(
                                    "Passwords do not match",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (isFormValid) viewModel.signUp(fullName, email, phoneNumber, password)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Terms & Conditions checkbox ───────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PurpleLight,
                                uncheckedColor = TextMuted,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I agree to the ",
                            fontFamily = InterRegular,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Terms",
                            modifier = Modifier.clickable { /* Open terms screen */ },
                            fontFamily = InterRegular,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurpleLight
                        )
                        Text(
                            text = " & ",
                            fontFamily = InterRegular,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Privacy",
                            modifier = Modifier.clickable { /* Open privacy screen */ },
                            fontFamily = InterRegular,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurpleLight
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Sign Up button ────────────────────────────────────────
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.signUp(fullName, email, phoneNumber, password)
                        },
                        enabled = isFormValid && authState !is AuthState.Loading,
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
                                    if (authState is AuthState.Loading || !isFormValid)
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
                                    text = "Create Account",
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

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer ────────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Already have an account?",
                    fontFamily = InterRegular,
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Login",
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTES.LOGIN) {
                            popUpTo(ROUTES.SIGNUP) { inclusive = true }
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