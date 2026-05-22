package com.rocky.filmtv.ui.player.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed

enum class PlayerIconType {
    PREV,
    REWIND,
    PLAY,
    PAUSE,
    FAST_FORWARD,
    NEXT,
    EPISODES,
    ASPECT_RATIO_FIT,
    ASPECT_RATIO_ZOOM,
    ASPECT_RATIO_FILL
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerControlButton(
    iconType: PlayerIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    size: Dp = 50.dp
) {
    val isPlayPause = iconType == PlayerIconType.PLAY || iconType == PlayerIconType.PAUSE
    Button(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .let { if (focusRequester != null) it.focusRequester(focusRequester) else it },
        shape = ButtonDefaults.shape(shape = CircleShape),
        colors = ButtonDefaults.colors(
            containerColor = if (isPlayPause) PrimaryRed else Color(0xFF172A45).copy(alpha = 0.8f),
            contentColor = if (isPlayPause) Color.Black else Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            focusedContentColor = Color(0xFF0A192F)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val contentColor = LocalContentColor.current
            Canvas(modifier = Modifier.size(24.dp)) {
                val width = this.size.width
                val height = this.size.height
                when (iconType) {
                    PlayerIconType.PREV -> {
                        // Left bar
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(width * 0.22f, height * 0.25f),
                            size = Size(width * 0.08f, height * 0.5f)
                        )
                        // Left-pointing triangle
                        val path = Path().apply {
                            moveTo(width * 0.35f, height * 0.5f)
                            lineTo(width * 0.78f, height * 0.25f)
                            lineTo(width * 0.78f, height * 0.75f)
                            close()
                        }
                        drawPath(path, color = contentColor)
                    }
                    PlayerIconType.NEXT -> {
                        // Right-pointing triangle
                        val path = Path().apply {
                            moveTo(width * 0.65f, height * 0.5f)
                            lineTo(width * 0.22f, height * 0.25f)
                            lineTo(width * 0.22f, height * 0.75f)
                            close()
                        }
                        drawPath(path, color = contentColor)
                        // Right bar
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(width * 0.7f, height * 0.25f),
                            size = Size(width * 0.08f, height * 0.5f)
                        )
                    }
                    PlayerIconType.REWIND -> {
                        // Left triangle 1
                        val path1 = Path().apply {
                            moveTo(width * 0.15f, height * 0.5f)
                            lineTo(width * 0.5f, height * 0.25f)
                            lineTo(width * 0.5f, height * 0.75f)
                            close()
                        }
                        drawPath(path1, color = contentColor)
                        // Left triangle 2
                        val path2 = Path().apply {
                            moveTo(width * 0.5f, height * 0.5f)
                            lineTo(width * 0.85f, height * 0.25f)
                            lineTo(width * 0.85f, height * 0.75f)
                            close()
                        }
                        drawPath(path2, color = contentColor)
                    }
                    PlayerIconType.FAST_FORWARD -> {
                        // Right triangle 1
                        val path1 = Path().apply {
                            moveTo(width * 0.5f, height * 0.5f)
                            lineTo(width * 0.15f, height * 0.25f)
                            lineTo(width * 0.15f, height * 0.75f)
                            close()
                        }
                        drawPath(path1, color = contentColor)
                        // Right triangle 2
                        val path2 = Path().apply {
                            moveTo(width * 0.85f, height * 0.5f)
                            lineTo(width * 0.5f, height * 0.25f)
                            lineTo(width * 0.5f, height * 0.75f)
                            close()
                        }
                        drawPath(path2, color = contentColor)
                    }
                    PlayerIconType.PLAY -> {
                        // Right-pointing triangle (offset slightly right to feel balanced)
                        val path = Path().apply {
                            moveTo(width * 0.82f, height * 0.5f)
                            lineTo(width * 0.28f, height * 0.22f)
                            lineTo(width * 0.28f, height * 0.78f)
                            close()
                        }
                        drawPath(path, color = contentColor)
                    }
                    PlayerIconType.PAUSE -> {
                        // Two vertical bars
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(width * 0.28f, height * 0.22f),
                            size = Size(width * 0.14f, height * 0.56f)
                        )
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(width * 0.58f, height * 0.22f),
                            size = Size(width * 0.14f, height * 0.56f)
                        )
                    }
                    PlayerIconType.EPISODES -> {
                        val thickness = 2.dp.toPx()
                        val bulletSize = 3.dp.toPx()
                        
                        // Row 1
                        drawCircle(color = contentColor, radius = bulletSize / 2, center = Offset(width * 0.25f, height * 0.32f))
                        drawLine(color = contentColor, start = Offset(width * 0.4f, height * 0.32f), end = Offset(width * 0.8f, height * 0.32f), strokeWidth = thickness)
                        
                        // Row 2
                        drawCircle(color = contentColor, radius = bulletSize / 2, center = Offset(width * 0.25f, height * 0.5f))
                        drawLine(color = contentColor, start = Offset(width * 0.4f, height * 0.5f), end = Offset(width * 0.8f, height * 0.5f), strokeWidth = thickness)
                        
                        // Row 3
                        drawCircle(color = contentColor, radius = bulletSize / 2, center = Offset(width * 0.25f, height * 0.68f))
                        drawLine(color = contentColor, start = Offset(width * 0.4f, height * 0.68f), end = Offset(width * 0.8f, height * 0.68f), strokeWidth = thickness)
                    }
                    PlayerIconType.ASPECT_RATIO_FIT,
                    PlayerIconType.ASPECT_RATIO_ZOOM,
                    PlayerIconType.ASPECT_RATIO_FILL -> {
                        // TV Frame outer
                        drawRoundRect(
                            color = contentColor,
                            topLeft = Offset(width * 0.1f, height * 0.12f),
                            size = Size(width * 0.8f, height * 0.56f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        // TV Stand / Leg
                        val legPath = Path().apply {
                            moveTo(width * 0.35f, height * 0.84f)
                            lineTo(width * 0.65f, height * 0.84f)
                            moveTo(width * 0.5f, height * 0.68f)
                            lineTo(width * 0.5f, height * 0.84f)
                        }
                        drawPath(legPath, color = contentColor, style = Stroke(width = 1.5.dp.toPx()))
                    }
                }
            }

            if (iconType == PlayerIconType.ASPECT_RATIO_FIT ||
                iconType == PlayerIconType.ASPECT_RATIO_ZOOM ||
                iconType == PlayerIconType.ASPECT_RATIO_FILL
            ) {
                val letter = when (iconType) {
                    PlayerIconType.ASPECT_RATIO_FIT -> "F"
                    PlayerIconType.ASPECT_RATIO_ZOOM -> "Z"
                    PlayerIconType.ASPECT_RATIO_FILL -> "S"
                    else -> ""
                }
                Text(
                    text = letter,
                    color = contentColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}
