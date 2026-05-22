package com.rocky.filmtv.ui.category.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.ui.home.components.MovieCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategoryMovieGrid(
    displayTitle: String,
    uiState: UiState<List<Movie>>,
    selectedYear: String,
    onMovieClick: (Movie) -> Unit,
    onRetryClick: () -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Header (Display dynamic title)
        Text(
            text = displayTitle,
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Category list Grid
        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00D2FF))
                }
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(onClick = onRetryClick) {
                        Text("Thử Lại")
                    }
                }
            }
            is UiState.Success -> {
                val movies = uiState.data
                val filteredMovies = remember(movies, selectedYear) {
                    movies.filter { movie ->
                        when (selectedYear) {
                            "Tất Cả" -> true
                            "Trước 2019" -> movie.year < 2019
                            else -> movie.year.toString() == selectedYear
                        }
                    }
                }

                if (filteredMovies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không tìm thấy phim nào khớp với bộ lọc năm $selectedYear",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredMovies) { movie ->
                            MovieCard(
                                movie = movie,
                                onMovieClick = { onMovieClick(movie) }
                            )
                        }

                        // D-pad Load More Button at bottom of the grid
                        item(span = { GridItemSpan(4) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = onLoadNextPage,
                                    colors = ButtonDefaults.colors(
                                        containerColor = Color(0xFF2C2C2E),
                                        contentColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        focusedContentColor = Color.Black
                                    )
                                ) {
                                    Text("Xem Thêm Phim", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
