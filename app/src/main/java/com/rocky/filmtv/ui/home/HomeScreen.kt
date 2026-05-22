package com.rocky.filmtv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.ui.home.components.FeaturedBanner
import com.rocky.filmtv.ui.home.components.MovieRow
import com.rocky.filmtv.ui.home.components.TopMenuBar
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import com.rocky.filmtv.ui.home.components.UpdateDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToPlayer: (String, Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCategory: (String, String, String) -> Unit,
    onNavigateToGenreList: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val featuredMovie by viewModel.featuredMovie.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.checkAppUpdate()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else if (state.isError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .focusProperties {
                        if (updateState !is UpdateUiState.NoUpdate) {
                            canFocus = false
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage ?: "Đã xảy ra lỗi kết nối mạng",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = { viewModel.loadHomeData() }) {
                    Text(text = "Thử Lại")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .focusProperties {
                        if (updateState !is UpdateUiState.NoUpdate) {
                            canFocus = false
                        }
                    }
            ) {
                // TV-Safe Top Menu Bar (Fixed at the top)
                TopMenuBar(
                    onNavigateToSearch = onNavigateToSearch,
                    onNavigateToFavorite = onNavigateToFavorite,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToGenreList = onNavigateToGenreList,
                    onNavigateToCategory = onNavigateToCategory,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.hasFocus) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    }
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 48.dp)
                ) {
                    // Cinematic FeaturedBanner
                    item {
                        FeaturedBanner(
                            activeMovie = featuredMovie,
                            fallbackList = state.phimMoi.list,
                            onPlayClick = { movie -> onNavigateToPlayer(movie.slug, 0) },
                            onDetailClick = { movie -> onNavigateToDetail(movie.slug) },
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .onFocusChanged { focusState ->
                                    if (focusState.hasFocus) {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(0)
                                        }
                                    }
                                }
                        )
                    }

                    // Recently Updated Movies
                    item {
                        MovieRow(
                            title = "Phim Mới Cập Nhật",
                            movies = state.phimMoi,
                            onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                            onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) },
                            onSeeAllClick = { onNavigateToCategory("phim-moi", "phim-moi", "Phim Mới Cập Nhật") }
                        )
                    }

                    // Phim Lẻ
                    item {
                        MovieRow(
                            title = "Phim Lẻ Nổi Bật",
                            movies = state.phimLe,
                            onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                            onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) },
                            onSeeAllClick = { onNavigateToCategory("type", "phim-le", "Phim Lẻ") }
                        )
                    }

                    // Phim Bộ
                    item {
                        MovieRow(
                            title = "Phim Bộ Đặc Sắc",
                            movies = state.phimBo,
                            onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                            onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) },
                            onSeeAllClick = { onNavigateToCategory("type", "phim-bo", "Phim Bộ") }
                        )
                    }

                    // Hoạt Hình
                    item {
                        MovieRow(
                            title = "Hoạt Hình & Anime",
                            movies = state.hoatHinh,
                            onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                            onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) },
                            onSeeAllClick = { onNavigateToCategory("type", "hoat-hinh", "Hoạt Hình") }
                        )
                    }

                    // TV Shows
                    item {
                        MovieRow(
                            title = "TV Shows hấp dẫn",
                            movies = state.tvShows,
                            onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                            onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) },
                            onSeeAllClick = { onNavigateToCategory("type", "tv-shows", "TV Shows") }
                        )
                    }
                }
            }
        }

        // Premium Dark-Themed TV Auto-Update Dialog
        UpdateDialog(
            state = updateState,
            onUpdateClick = { info -> viewModel.startDownload(info.downloadUrl) },
            onDismissClick = { viewModel.dismissUpdate() }
        )
    }
}
