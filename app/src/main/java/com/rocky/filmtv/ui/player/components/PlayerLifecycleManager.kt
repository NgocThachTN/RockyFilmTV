package com.rocky.filmtv.ui.player.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.exoplayer.ExoPlayer
import timber.log.Timber

@Composable
fun PlayerLifecycleManager(
    exoPlayer: ExoPlayer?,
    onSaveHistory: (position: Long, duration: Long) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentExoPlayer by rememberUpdatedState(exoPlayer)
    val currentOnSaveHistory by rememberUpdatedState(onSaveHistory)

    // Release and Save history on screen dispose
    DisposableEffect(lifecycleOwner) {
        onDispose {
            currentExoPlayer?.let { player ->
                val pos = player.currentPosition
                val dur = player.duration
                if (dur > 0) {
                    currentOnSaveHistory(pos, dur)
                }
                player.release()
                Timber.d("ExoPlayer successfully released and progress saved on dispose.")
            }
        }
    }

    // Lifecycle Observer for Backgrounding / Foregrounding
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    currentExoPlayer?.let { player ->
                        player.pause()
                        val pos = player.currentPosition
                        val dur = player.duration
                        if (dur > 0) {
                            currentOnSaveHistory(pos, dur)
                        }
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    currentExoPlayer?.play()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
