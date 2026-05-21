package com.rocky.filmtv.ui.player

import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PlayerScreen(
    movieSlug: String,
    episodeIndex: Int,
    onNavigateBack: () -> Unit,
    onPlayEpisode: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var bufferPercentage by remember { mutableStateOf(0) }
    var isBuffering by remember { mutableStateOf(true) }
    var playerError by remember { mutableStateOf<String?>(null) }

    // Controller overlay visible state
    var showControls by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Safely capture the latest exoPlayer state to prevent leaking or releasing active player on recompositions
    val currentExoPlayer by rememberUpdatedState(exoPlayer)

    // Load state
    LaunchedEffect(movieSlug, episodeIndex) {
        viewModel.loadEpisode(movieSlug, episodeIndex)
    }

    // Controls Auto-Hide timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(5000) // Hide after 5 seconds of active playback
            showControls = false
        }
    }

    // Release and Save history on screen dispose (keyed on Unit/lifecycleOwner to only run once)
    DisposableEffect(lifecycleOwner) {
        onDispose {
            currentExoPlayer?.let { player ->
                val pos = player.currentPosition
                val dur = player.duration
                if (dur > 0) {
                    viewModel.saveHistory(pos, dur)
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
                            viewModel.saveHistory(pos, dur)
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

    // ExoPlayer Builder and MediaItem Loader
    LaunchedEffect(state.currentEpisode) {
        val episode = state.currentEpisode ?: return@LaunchedEffect
        val streamUrl = episode.linkM3u8.ifEmpty { return@LaunchedEffect }

        Timber.d("Initializing ExoPlayer streaming URL: $streamUrl")
        
        // Release old player if exists
        exoPlayer?.release()
        playerError = null // Reset playback error on episode load

        // Configure HLS HTTP data source with proper User-Agent and Referer headers to bypass anti-hotlinking
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(mapOf(
                "Referer" to "https://ophim1.com/"
            ))
        
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        val player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                val mediaItem = MediaItem.Builder()
                    .setUri(streamUrl)
                    .setMimeType(MimeTypes.APPLICATION_M3U8) // Support HLS streaming out-of-the-box
                    .build()
                
                setMediaItem(mediaItem)
                prepare()
                seekTo(state.initialPosition)
                playWhenReady = true
            }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    totalDuration = player.duration
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                currentPosition = player.currentPosition
            }

            override fun onPlayerError(error: PlaybackException) {
                Timber.e(error, "ExoPlayer playback error occurred playing $streamUrl")
                playerError = "Lỗi phát video: " + (error.localizedMessage ?: "Không thể tải luồng video")
            }
        })

        exoPlayer = player
    }

    // Position updates ticker
    LaunchedEffect(exoPlayer, isPlaying) {
        val player = exoPlayer
        if (player != null && isPlaying) {
            while (isActive) {
                currentPosition = player.currentPosition
                bufferPercentage = player.bufferedPercentage
                delay(1000)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            // Listen to TV D-pad clicks to toggle overlays and handle navigation
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                            if (!showControls) {
                                showControls = true
                                true
                            } else false
                        }
                        Key.DirectionLeft -> {
                            if (!showControls) {
                                // Direct fast rewind (-10s)
                                exoPlayer?.let { player ->
                                    val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                                    player.seekTo(newPos)
                                    currentPosition = newPos
                                }
                                showControls = true
                                true
                            } else false
                        }
                        Key.DirectionRight -> {
                            if (!showControls) {
                                // Direct fast forward (+10s)
                                exoPlayer?.let { player ->
                                    val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                                    player.seekTo(newPos)
                                    currentPosition = newPos
                                }
                                showControls = true
                                true
                            } else false
                        }
                        Key.Back -> {
                            if (showControls) {
                                showControls = false
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
            .focusable()
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else if (state.isError || playerError != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = playerError ?: state.errorMessage ?: "Lỗi tải video",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = {
                    playerError = null
                    viewModel.loadEpisode(movieSlug, episodeIndex)
                }) {
                    Text("Thử Lại")
                }
            }
        } else {
            // 1. AndroidView wrapping ExoPlayer PlayerView
            exoPlayer?.let { player ->
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false // We build our own stunning custom Compose controller!
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setPlayer(player)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Custom Cinematic Controller Overlays
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
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
                                text = state.movieDetail?.name ?: "",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Đang phát: ${state.currentEpisode?.name ?: ""}",
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = formatTime(currentPosition),
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Slider(
                                value = progress,
                                onValueChange = {},
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryRed,
                                    activeTrackColor = PrimaryRed,
                                    inactiveTrackColor = Color.DarkGray
                                )
                            )

                            Text(
                                text = formatTime(totalDuration),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Controls Row: Prev, Rewind, Play/Pause, FastForward, Next
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous Episode Button
                            if (state.isHasPrev) {
                                ControlButton(
                                    text = "⏮ Tập Trước",
                                    onClick = { onPlayEpisode(movieSlug, episodeIndex - 1) }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            // Rewind 10s Button
                            ControlButton(
                                text = "⏪ -10s",
                                onClick = {
                                    exoPlayer?.let { player ->
                                        val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                                        player.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // Play/Pause Button
                            Button(
                                onClick = {
                                    exoPlayer?.let { player ->
                                        if (player.isPlaying) player.pause() else player.play()
                                    }
                                },
                                colors = ButtonDefaults.colors(
                                    containerColor = PrimaryRed,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .width(100.dp)
                            ) {
                                Text(
                                    text = if (isPlaying) "⏸ Tạm Dừng" else "▶ Phát",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Fast Forward 10s Button
                            ControlButton(
                                text = "+10s ⏩",
                                onClick = {
                                    exoPlayer?.let { player ->
                                        val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                                        player.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                }
                            )

                            if (state.isHasNext) {
                                Spacer(modifier = Modifier.width(16.dp))
                                // Next Episode Button
                                ControlButton(
                                    text = "Tập Tiếp ⏭",
                                    onClick = { onPlayEpisode(movieSlug, episodeIndex + 1) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto-focus Play/Pause button when control shows up to let TV users immediately click
    LaunchedEffect(showControls) {
        if (showControls) {
            try {
                // Wait for the AnimatedVisibility layout to attach the button to the tree
                delay(150)
                focusRequester.requestFocus()
            } catch (e: Exception) {
                Timber.e("Failed to request focus on controls: ${e.message}")
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ControlButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(
            containerColor = Color(0xFF2C2C2E),
            contentColor = Color.White,
            focusedContainerColor = Color.White,
            focusedContentColor = Color.Black
        ),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSecs = ms / 1000
    val hours = totalSecs / 3600
    val minutes = (totalSecs % 3600) / 60
    val seconds = totalSecs % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
