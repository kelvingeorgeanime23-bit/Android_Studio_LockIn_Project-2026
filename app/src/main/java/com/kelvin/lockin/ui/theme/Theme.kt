package com.kelvin.lockin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LockInColorScheme = darkColorScheme(
    // BACKGROUNDS
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardDark,

    // PRIMARY PURPLE
    primary = PurplePrimary,
    onPrimary = TextWhite,
    primaryContainer = PurpleGlow,
    onPrimaryContainer = TextWhite,

    // SECONDARY PURPLE
    secondary = PurpleLight,
    onSecondary = TextWhite,

    // TEXT
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextGrey,

    // STATUS
    error = ErrorRed,
    onError = TextWhite,
)

@Composable
fun LockInTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LockInColorScheme,
        typography = Typography,
        content = content
    )
}
