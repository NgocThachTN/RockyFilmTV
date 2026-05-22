package com.rocky.filmtv.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed
import androidx.media3.ui.AspectRatioFrameLayout

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerControlsOverlay(
    showControls: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    movieTitle: String,
    episodeTitle: String,
    isHasPrev: Boolean,
    isHasNext: Boolean,
    resizeMode: Int,
    onNavigateBack: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onRewindClick: () -> Unit,
    onFastForwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onEpisodesClick: () -> Unit,
    onResizeModeClick: () -> Unit,
    onSeekLeft: () -> Unit,
    onSeekRight: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = showControls,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(32.dp)
        ) {
            // Top Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = movieTitle,
                        color = Color.White,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tập: $episodeTitle",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }

                // Close Screen Button
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text("Thoát Player")
                }
            }

            // Loading/Buffering status indicator
            if (isBuffering) {
                CircularProgressIndicator(
                    color = PrimaryRed,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }

            // Bottom Control Layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // Slider / Progress Track
                val progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f
                var isSliderFocused by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isSliderFocused = it.isFocused }
                        .focusable()
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                when (keyEvent.key) {
                                    Key.DirectionLeft -> {
                                        onSeekLeft()
                                        true
                                    }
                                    Key.DirectionRight -> {
                                        onSeekRight()
                                        true
                                    }
                                    else -> false
                                }
                            } else false
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        color = if (isSliderFocused) Color(0xFF00D2FF) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSliderFocused) FontWeight.Bold else FontWeight.Normal
                    )

                    Slider(
                        value = progress,
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = if (isSliderFocused) Color(0xFF00D2FF) else PrimaryRed,
                            activeTrackColor = if (isSliderFocused) Color(0xFF00D2FF) else PrimaryRed,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )

                    Text(
                        text = formatTime(totalDuration),
                        color = if (isSliderFocused) Color(0xFF00D2FF) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSliderFocused) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controls Row: Prev, Rewind, Play/Pause, FastForward, Next, Aspect Ratio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Episode Button
                    if (isHasPrev) {
                        PlayerControlButton(
                            iconType = PlayerIconType.PREV,
                            onClick = onPrevClick
                        )
                    }

                    // Rewind 10s Button
                    PlayerControlButton(
                        iconType = PlayerIconType.REWIND,
                        onClick = onRewindClick
                    )

                    // Play/Pause Button
                    PlayerControlButton(
                        iconType = if (isPlaying) PlayerIconType.PAUSE else PlayerIconType.PLAY,
                        onClick = onPlayPauseClick,
                        focusRequester = focusRequester,
                        size = 58.dp
                    )

                    // Fast Forward 10s Button
                    PlayerControlButton(
                        iconType = PlayerIconType.FAST_FORWARD,
                        onClick = onFastForwardClick
                    )

                    // Next Episode Button
                    if (isHasNext) {
                        PlayerControlButton(
                            iconType = PlayerIconType.NEXT,
                            onClick = onNextClick
                        )
                    }

                    // Episodes List Selection Button
                    PlayerControlButton(
                        iconType = PlayerIconType.EPISODES,
                        onClick = onEpisodesClick
                    )

                    // Aspect Ratio Mode Switcher Button
                    val aspectIcon = when (resizeMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> PlayerIconType.ASPECT_RATIO_FIT
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> PlayerIconType.ASPECT_RATIO_ZOOM
                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> PlayerIconType.ASPECT_RATIO_FILL
                        else -> PlayerIconType.ASPECT_RATIO_FIT
                    }
                    PlayerControlButton(
                        iconType = aspectIcon,
                        onClick = onResizeModeClick
                    )
                }
            }
        }
    }
}
