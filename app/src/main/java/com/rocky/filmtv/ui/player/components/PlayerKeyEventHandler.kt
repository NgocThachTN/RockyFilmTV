package com.rocky.filmtv.ui.player.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.media3.exoplayer.ExoPlayer

fun Modifier.handlePlayerKeyEvent(
    exoPlayer: ExoPlayer?,
    showControls: Boolean,
    showEpisodeList: Boolean,
    onShowControls: (Boolean) -> Unit,
    onShowEpisodeList: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onPositionChanged: (Long) -> Unit
): Modifier = this.onKeyEvent { keyEvent ->
    if (keyEvent.type == KeyEventType.KeyDown) {
        when (keyEvent.key) {
            Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                if (!showControls && !showEpisodeList) {
                    onShowControls(true)
                    true
                } else false
            }
            Key.DirectionUp, Key.DirectionDown -> {
                if (!showControls && !showEpisodeList) {
                    onShowControls(true)
                    true
                } else false
            }
            Key.DirectionLeft -> {
                if (!showControls && !showEpisodeList) {
                    // Direct fast rewind (-10s)
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                        player.seekTo(newPos)
                        onPositionChanged(newPos)
                    }
                    onShowControls(true)
                    true
                } else false
            }
            Key.DirectionRight -> {
                if (!showControls && !showEpisodeList) {
                    // Direct fast forward (+10s)
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                        player.seekTo(newPos)
                        onPositionChanged(newPos)
                    }
                    onShowControls(true)
                    true
                } else false
            }
            Key.Back -> {
                if (showEpisodeList) {
                    onShowEpisodeList(false)
                    onShowControls(true)
                    true
                } else if (showControls) {
                    onShowControls(false)
                    true
                } else {
                    onNavigateBack()
                    true
                }
            }
            else -> false
        }
    } else false
}
