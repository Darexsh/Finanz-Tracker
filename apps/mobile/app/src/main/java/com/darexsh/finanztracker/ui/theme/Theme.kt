package com.darexsh.finanztracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darexsh.finanztracker.model.FontSizeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    background = Color(0xFFF6FBFF),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEFF5FF),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFD7E2EE),
    error = Color(0xFFDC2626),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF14B8A6),
    secondary = Color(0xFF22D3EE),
    background = Color(0xFF0B1320),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB),
    onBackground = Color(0xFFE5E7EB)
)

private fun appTypography(scale: Float): Typography {
    return Typography(
        headlineSmall = TextStyle(fontSize = (24f * scale).sp, fontWeight = FontWeight.SemiBold),
        titleMedium = TextStyle(fontSize = (18f * scale).sp, fontWeight = FontWeight.SemiBold),
        labelLarge = TextStyle(fontSize = (14f * scale).sp, fontWeight = FontWeight.Medium),
        bodyMedium = TextStyle(fontSize = (14f * scale).sp),
        bodySmall = TextStyle(fontSize = (12f * scale).sp)
    )
}

@Composable
fun FinanzTrackerTheme(
    darkTheme: Boolean = false,
    fontSizeMode: FontSizeMode = FontSizeMode.NORMAL,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val scale = when (fontSizeMode) {
        FontSizeMode.COMPACT -> 0.92f
        FontSizeMode.NORMAL -> 1f
        FontSizeMode.LARGE -> 1.1f
    }
    MaterialTheme(
        colorScheme = colors,
        typography = appTypography(scale),
        content = content
    )
}
