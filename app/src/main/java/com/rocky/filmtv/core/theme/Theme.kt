package com.rocky.filmtv.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.*

val PrimaryRed = Color(0xFF00D2FF) // Vibrant light sea blue / bright cyan accent
val DarkBackground = Color(0xFF0A192F) // Deep ocean midnight blue background
val SurfaceDark = Color(0xFF172A45) // Marine navy surface card container
val BorderFocused = Color(0xFF00D2FF) // Glowing cyan focus border

private val DarkTvColorScheme = darkColorScheme(
    primary = PrimaryRed,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = Color.Black, // Dark text on bright cyan primary buttons
    onBackground = Color(0xFFF1F5F9), // Light slate color for text
    onSurface = Color(0xFFF1F5F9), // Light slate color for card text
    border = Color(0xFF1B355A) // Oceanic border color
)

@Composable
fun RockyFilmTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkTvColorScheme,
        content = content
    )
}
