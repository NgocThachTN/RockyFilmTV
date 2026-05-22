package com.rocky.filmtv.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.tv.material3.*
import com.rocky.filmtv.ui.player.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

    // Reset controls auto-hide timer on user interaction/seeking
    var controlsInteractionTrigger by remember { mutableStateOf(0) }

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

    // Lifecycle and memory management via PlayerLifecycleManager
    PlayerLifecycleManager(
        exoPlayer = exoPlayer,
        onSaveHistory = { pos, dur -> viewModel.saveHistory(pos, dur) }
    )

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
            .handlePlayerKeyEvent(
                exoPlayer = exoPlayer,
                showControls = showControls,
                showEpisodeList = showEpisodeList,
                onShowControls = { showControls = it },
                onShowEpisodeList = { showEpisodeList = it },
                onNavigateBack = onNavigateBack,
                onPositionChanged = { currentPosition = it }
            )
            .focusable()
    ) {
        if (state.isLoading) {
            PlayerLoadingView()
        } else if (state.isError || playerError != null) {
            PlayerErrorView(
                errorText = playerError ?: state.errorMessage ?: "Lỗi tải video",
                onRetryClick = {
                    playerError = null
                    viewModel.loadEpisode(movieSlug, episodeIndex)
                }
            )
        } else {
            // 1. Adaptive Video Surface wrapping ExoPlayer PlayerView
            PlayerSurface(
                exoPlayer = exoPlayer,
                resizeMode = resizeMode,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Custom Cinematic Controller Overlays
            PlayerControlsOverlay(
                showControls = showControls,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                movieTitle = state.movieDetail?.name ?: "",
                episodeTitle = state.currentEpisode?.name ?: "",
                isHasPrev = state.isHasPrev,
                isHasNext = state.isHasNext,
                resizeMode = resizeMode,
                onNavigateBack = onNavigateBack,
                onPrevClick = { onPlayEpisode(movieSlug, episodeIndex - 1) },
                onNextClick = { onPlayEpisode(movieSlug, episodeIndex + 1) },
                onRewindClick = {
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                        player.seekTo(newPos)
                        currentPosition = newPos
                    }
                    controlsInteractionTrigger++
                },
                onFastForwardClick = {
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                        player.seekTo(newPos)
                        currentPosition = newPos
                    }
                    controlsInteractionTrigger++
                },
                onPlayPauseClick = {
                    exoPlayer?.let { player ->
                        if (player.isPlaying) player.pause() else player.play()
                    }
                },
                onEpisodesClick = {
                    showEpisodeList = true
                    showControls = false
                },
                onResizeModeClick = {
                    resizeMode = when (resizeMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                onSeekLeft = {
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition - 10000).coerceAtLeast(0)
                        player.seekTo(newPos)
                        currentPosition = newPos
                    }
                    controlsInteractionTrigger++
                },
                onSeekRight = {
                    exoPlayer?.let { player ->
                        val newPos = (player.currentPosition + 10000).coerceAtMost(player.duration)
                        player.seekTo(newPos)
                        currentPosition = newPos
                    }
                    controlsInteractionTrigger++
                },
                focusRequester = focusRequester
            )

            // 3. Side Panel Episode List Drawer
            val episodes = state.movieDetail?.episodes?.firstOrNull()?.episodes ?: emptyList()
            EpisodeSelector(
                showEpisodeList = showEpisodeList,
                movieSlug = movieSlug,
                currentEpisodeIndex = state.currentEpisodeIndex,
                episodes = episodes,
                episodeListFocusRequester = episodeListFocusRequester,
                onPlayEpisode = onPlayEpisode,
                onCloseSelector = {
                    showEpisodeList = false
                    showControls = true
                }
            )
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
