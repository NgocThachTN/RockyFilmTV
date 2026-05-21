package com.rocky.filmtv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val featuredMovie by viewModel.featuredMovie.collectAsState()
    val listState = rememberLazyListState()

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
                modifier = Modifier.fillMaxSize(),
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                // TV-Safe Top Menu Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROCKY FILM TV",
                            color = PrimaryRed,
                            fontSize = 20.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onNavigateToSearch,
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                )
                            ) {
                                Text("Tìm Kiếm")
                            }

                            Button(
                                onClick = onNavigateToFavorite,
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                )
                            ) {
                                Text("Yêu Thích")
                            }

                            Button(
                                onClick = onNavigateToHistory,
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                )
                            ) {
                                Text("Lịch Sử Xem")
                            }

                            Button(
                                onClick = { onNavigateToCategory("phim-bo", "Phim Bộ") },
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                )
                            ) {
                                Text("Phim Bộ")
                            }

                            Button(
                                onClick = { onNavigateToCategory("phim-le", "Phim Lẻ") },
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                )
                            ) {
                                Text("Phim Lẻ")
                            }
                        }
                    }
                }

                // Cinematic FeaturedBanner
                item {
                    FeaturedBanner(
                        movie = featuredMovie,
                        onPlayClick = { movie -> onNavigateToDetail(movie.slug) },
                        onDetailClick = { movie -> onNavigateToDetail(movie.slug) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Recently Updated Movies
                item {
                    MovieRow(
                        title = "Phim Mới Cập Nhật",
                        movies = state.phimMoi,
                        onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                        onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) }
                    )
                }

                // Phim Lẻ
                item {
                    MovieRow(
                        title = "Phim Lẻ Nổi Bật",
                        movies = state.phimLe,
                        onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                        onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) }
                    )
                }

                // Phim Bộ
                item {
                    MovieRow(
                        title = "Phim Bộ Đặc Sắc",
                        movies = state.phimBo,
                        onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                        onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) }
                    )
                }

                // Hoạt Hình
                item {
                    MovieRow(
                        title = "Hoạt Hình & Anime",
                        movies = state.hoatHinh,
                        onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                        onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) }
                    )
                }

                // TV Shows
                item {
                    MovieRow(
                        title = "TV Shows hấp dẫn",
                        movies = state.tvShows,
                        onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                        onMovieFocused = { movie -> viewModel.setFeaturedMovie(movie) }
                    )
                }
            }
        }
    }
}
