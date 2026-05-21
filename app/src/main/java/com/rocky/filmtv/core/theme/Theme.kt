package com.rocky.filmtv.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.*

val PrimaryRed = Color(0xFFE50914)
val DarkBackground = Color(0xFF0F0F0F)
val SurfaceDark = Color(0xFF1C1C1E)
val BorderFocused = Color(0xFFFFFFFF)

private val DarkTvColorScheme = darkColorScheme(
    primary = PrimaryRed,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onBackground = Color(0xFFF3F3F3),
    onSurface = Color(0xFFF3F3F3),
    border = Color(0xFF2C2C2E)
)

@Composable
fun RockyFilmTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkTvColorScheme,
        content = content
    )
}
