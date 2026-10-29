package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TelkomRed,
    onPrimary = Color.White,
    primaryContainer = TelkomRedDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF94A3B8),
    onSecondary = TelkomDark,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    error = StatusRejected,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TelkomRed,
    onPrimary = Color.White,
    primaryContainer = TelkomRedLight,
    onPrimaryContainer = TelkomRedDark,
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = TelkomTextPrimary,
    surface = Color.White,
    onSurface = TelkomTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TelkomTextSecondary,
    outline = TelkomBorder,
    error = StatusRejected,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
