package com.rocky.filmtv.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.core.utils.stripHtml
import com.rocky.filmtv.ui.detail.components.DetailInfoPanel
import com.rocky.filmtv.ui.detail.components.FlowRow

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailScreen(
    slug: String,
    onNavigateToPlayer: (String, Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    LaunchedEffect(slug) {
        viewModel.loadMovieDetail(slug)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (val uiState = state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.message,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(onClick = { viewModel.loadMovieDetail(slug) }) {
                        Text(text = "Thử Lại")
                    }
                }
            }
            is UiState.Success -> {
                val detail = uiState.data
                
                // 1. Cinematic Backdrop blur background
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = detail.thumbUrl.ifEmpty { detail.posterUrl },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(20.dp)
                            .alpha(0.3f),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black
                                    )
                                )
                            )
                    )
                }

                // 2. Scrollable Detail content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Back Nav Button
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.colors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedContentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "← Quay Lại",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Poster Card left column
                        Box(
                            modifier = Modifier
                                .width(220.dp)
                                .height(310.dp)
                        ) {
                            AsyncImage(
                                model = detail.posterUrl.ifEmpty { detail.thumbUrl },
                                contentDescription = detail.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Right Column Details
                        DetailInfoPanel(
                            detail = detail,
                            isFavorite = isFavorite,
                            watchHistory = watchHistory,
                            onNavigateToPlayer = onNavigateToPlayer,
                            onToggleFavorite = { viewModel.toggleFavorite() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Movie Plot Synopsis
                    Text(
                        text = "Nội Dung Phim",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = detail.content.stripHtml().ifEmpty { "Đang cập nhật nội dung..." },
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. Episodes Grid / List
                    Text(
                        text = "Danh Sách Tập Phim",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (detail.episodes.isEmpty()) {
                        Text(text = "Đang cập nhật tập phim...", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        detail.episodes.forEach { server ->
                            if (detail.episodes.size > 1) {
                                Text(
                                    text = server.serverName,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            // Horizontal grid of episode boxes
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                mainAxisSpacing = 8.dp,
                                crossAxisSpacing = 8.dp
                            ) {
                                server.episodes.forEachIndexed { index, episode ->
                                    val isCurrentHistory = watchHistory?.lastWatchedEpisodeSlug == episode.slug
                                    
                                    Button(
                                        onClick = { onNavigateToPlayer(detail.slug, index) },
                                        colors = ButtonDefaults.colors(
                                            containerColor = if (isCurrentHistory) PrimaryRed.copy(alpha = 0.6f) else Color(0xFF2C2C2E),
                                            contentColor = Color.White,
                                            focusedContainerColor = Color.White,
                                            focusedContentColor = Color.Black
                                        ),
                                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            text = episode.name,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
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
