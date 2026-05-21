package com.rocky.filmtv.ui.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
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
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.ui.home.components.MovieCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val movies by viewModel.favorites.collectAsState()

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
                    text = "Danh Sách Yêu Thích",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Movies Grid
            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Danh sách yêu thích trống. Hãy bấm 'Yêu Thích' ở chi tiết phim.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
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
                }
            }
        }
    }
}
