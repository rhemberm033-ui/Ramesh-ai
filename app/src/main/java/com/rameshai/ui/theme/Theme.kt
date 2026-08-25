package com.rameshai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurplePrimary = Color(0xFF7C4DFF)
val BluePrimary = Color(0xFF448AFF)
val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF1A1A2E)
val LightBackground = Color(0xFFF6F5FA)
val LightSurface = Color(0xFFFFFFFF)

private val DarkColors = darkColorScheme(
    primary = PurplePrimary,
    secondary = BluePrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onBackground = Color(0xFFECECF4),
    onSurface = Color(0xFFECECF4)
)

private val LightColors = lightColorScheme(
    primary = PurplePrimary,
    secondary = BluePrimary,
    background = LightBackground,
    surface = LightSurface
)

@Composable
fun RameshAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
