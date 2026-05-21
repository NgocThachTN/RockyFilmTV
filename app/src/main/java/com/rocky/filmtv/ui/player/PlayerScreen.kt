package com.rocky.filmtv.ui.player

import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    var resizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }



    // Controller overlay visible state
    var showControls by remember { mutableStateOf(true) }
    var showEpisodeList by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val episodeListFocusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Reset controls auto-hide timer on user interaction/seeking
    var controlsInteractionTrigger by remember { mutableStateOf(0) }

    // Safely capture the latest exoPlayer state to prevent leaking or releasing active player on recompositions
    val currentExoPlayer by rememberUpdatedState(exoPlayer)

    // Load state
    LaunchedEffect(movieSlug, episodeIndex) {
        viewModel.loadEpisode(movieSlug, episodeIndex)
    }

    // Controls Auto-Hide timer
    LaunchedEffect(showControls, isPlaying, controlsInteractionTrigger) {
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
                            if (!showControls && !showEpisodeList) {
                                showControls = true
                                true
                            } else false
                        }
                        Key.DirectionLeft -> {
                            if (!showControls && !showEpisodeList) {
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
                            if (!showControls && !showEpisodeList) {
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
                            if (showEpisodeList) {
                                showEpisodeList = false
                                showControls = true
                                true
                            } else if (showControls) {
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
                                                exoPlayer?.let { player ->
                                                    val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                                                    player.seekTo(newPos)
                                                    currentPosition = newPos
                                                }
                                                controlsInteractionTrigger++
                                                true
                                            }
                                            Key.DirectionRight -> {
                                                exoPlayer?.let { player ->
                                                    val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                                                    player.seekTo(newPos)
                                                    currentPosition = newPos
                                                }
                                                controlsInteractionTrigger++
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
                            if (state.isHasPrev) {
                                PlayerControlButton(
                                    iconType = PlayerIconType.PREV,
                                    onClick = { onPlayEpisode(movieSlug, episodeIndex - 1) }
                                )
                            }

                            // Rewind 10s Button
                            PlayerControlButton(
                                iconType = PlayerIconType.REWIND,
                                onClick = {
                                    exoPlayer?.let { player ->
                                        val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                                        player.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                }
                            )

                            // Play/Pause Button
                            PlayerControlButton(
                                iconType = if (isPlaying) PlayerIconType.PAUSE else PlayerIconType.PLAY,
                                onClick = {
                                    exoPlayer?.let { player ->
                                        if (player.isPlaying) player.pause() else player.play()
                                    }
                                },
                                focusRequester = focusRequester,
                                size = 58.dp
                            )

                            // Fast Forward 10s Button
                            PlayerControlButton(
                                iconType = PlayerIconType.FAST_FORWARD,
                                onClick = {
                                    exoPlayer?.let { player ->
                                        val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                                        player.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                }
                            )

                            // Next Episode Button
                            if (state.isHasNext) {
                                PlayerControlButton(
                                    iconType = PlayerIconType.NEXT,
                                    onClick = { onPlayEpisode(movieSlug, episodeIndex + 1) }
                                )
                            }

                            // Episodes List Selection Button
                            PlayerControlButton(
                                iconType = PlayerIconType.EPISODES,
                                onClick = {
                                    showEpisodeList = true
                                    showControls = false
                                }
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
                                onClick = {
                                    resizeMode = when (resizeMode) {
                                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 3. Side Panel Episode List Drawer
            AnimatedVisibility(
                visible = showEpisodeList,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(360.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A192F).copy(alpha = 0.95f))
                        .padding(vertical = 24.dp, horizontal = 20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Danh Sách Tập",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        val episodes = state.movieDetail?.episodes?.firstOrNull()?.episodes ?: emptyList()
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(episodes) { index, ep ->
                                val isCurrent = index == state.currentEpisodeIndex
                                Button(
                                    onClick = {
                                        showEpisodeList = false
                                        onPlayEpisode(movieSlug, index)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .let { 
                                            val shouldFocus = if (state.currentEpisodeIndex >= 0) isCurrent else index == 0
                                            if (shouldFocus) it.focusRequester(episodeListFocusRequester) else it
                                        },
                                    colors = ButtonDefaults.colors(
                                        containerColor = if (isCurrent) Color(0xFF172A45) else Color(0x33172A45),
                                        contentColor = if (isCurrent) PrimaryRed else Color.White,
                                        focusedContainerColor = Color.White,
                                        focusedContentColor = Color(0xFF0A192F)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ep.name,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        if (isCurrent) {
                                            Text(
                                                text = "Đang phát",
                                                fontSize = 11.sp,
                                                color = PrimaryRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
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

    // Auto-focus the active episode in the list when the drawer is shown
    LaunchedEffect(showEpisodeList) {
        if (showEpisodeList) {
            try {
                // Wait for the slide animation to attach the lazy list
                delay(150)
                episodeListFocusRequester.requestFocus()
            } catch (e: Exception) {
                Timber.e("Failed to focus episode list drawer: ${e.message}")
            }
        }
    }
}

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
