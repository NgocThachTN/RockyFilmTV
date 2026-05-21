package com.rocky.filmtv.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.ui.home.components.MovieCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategoryScreen(
    type: String,
    title: String,
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(type) {
        viewModel.loadCategory(type)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text("← Quay Lại")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Category list Grid
            when (val uiState = state) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
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
                        Button(onClick = { viewModel.fetchMovies() }) {
                            Text("Thử Lại")
                        }
                    }
                }
                is UiState.Success -> {
                    val movies = uiState.data
                    if (movies.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Danh sách trống", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(movies) { movie ->
                                MovieCard(
                                    movie = movie,
                                    onMovieClick = { onNavigateToDetail(it.slug) }
                                )
                            }

                            // D-pad Load More Button at bottom of the grid
                            item(span = { GridItemSpan(5) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        onClick = { viewModel.loadNextPage() },
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
}
