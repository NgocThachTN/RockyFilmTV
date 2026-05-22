package com.rocky.filmtv.ui.player.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun PlayerSurface(
    exoPlayer: ExoPlayer?,
    resizeMode: Int,
    modifier: Modifier = Modifier
) {
    exoPlayer?.let { player ->
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false // We build our own stunning custom Compose controller!
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setPlayer(player)
                }
            },
            update = { view ->
                view.resizeMode = resizeMode
            },
            modifier = modifier.fillMaxSize()
        )
    }
}
