package com.rocky.filmtv.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.remote.mapper.MovieDetail

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
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = detail.name,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "${detail.originName} (${detail.year})",
                                fontSize = 18.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Metadata Tags
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Tag(text = detail.quality)
                                Tag(text = detail.lang)
                                Tag(text = detail.status)
                                if (detail.time.isNotEmpty()) {
                                    Tag(text = detail.time)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Category Tags
                            Text(
                                text = "Thể loại: ${detail.categories.joinToString(", ")}",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Quốc gia: ${detail.countries.joinToString(", ")}",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                            if (detail.directors.isNotEmpty()) {
                                Text(
                                    text = "Đạo diễn: ${detail.directors.joinToString(", ")}",
                                    color = Color.LightGray,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        // Play first episode or resume last watched index
                                        val startIdx = watchHistory?.lastWatchedEpisodeSlug?.let { slug ->
                                            detail.episodes.firstOrNull()?.episodes?.indexOfFirst { it.slug == slug }
                                        }?.coerceAtLeast(0) ?: 0
                                        onNavigateToPlayer(detail.slug, startIdx)
                                    },
                                    colors = ButtonDefaults.colors(
                                        containerColor = PrimaryRed,
                                        contentColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        focusedContentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                                ) {
                                    val btnText = if (watchHistory != null) {
                                        "Tiếp Tục Xem (${watchHistory?.lastWatchedEpisodeName})"
                                    } else "Xem Ngay"
                                    Text(
                                        text = btnText,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }

                                Button(
                                    onClick = { viewModel.toggleFavorite() },
                                    colors = ButtonDefaults.colors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        focusedContentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    val favText = if (isFavorite) "♥ Đã Thích" else "♡ Yêu Thích"
                                    Text(
                                        text = favText,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
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
                        text = detail.content.ifEmpty { "Đang cập nhật nội dung..." },
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

@Composable
fun Tag(text: String, modifier: Modifier = Modifier) {
    if (text.isEmpty()) return
    Box(
        modifier = modifier
            .background(Color(0xFF2C2C2E), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Minimalist FlowRow implementation for compose layouts
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val mainSpacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()
        
        var rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var rowWidths = mutableListOf<Int>()
        var rowHeights = mutableListOf<Int>()
        
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        var currentRowHeight = 0
        
        for (measurable in measurables) {
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            if (currentRowWidth + placeable.width + mainSpacingPx > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                rowWidths.add(currentRowWidth - mainSpacingPx)
                rowHeights.add(currentRowHeight)
                
                currentRow = mutableListOf()
                currentRowWidth = 0
                currentRowHeight = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + mainSpacingPx
            currentRowHeight = maxOf(currentRowHeight, placeable.height)
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowWidths.add(currentRowWidth - mainSpacingPx)
            rowHeights.add(currentRowHeight)
        }
        
        val width = maxOf(rowWidths.maxOrNull() ?: 0, constraints.minWidth)
        val height = maxOf(rowHeights.sum() + crossSpacingPx * (rows.size - 1), constraints.minHeight)
        
        layout(width, height) {
            var y = 0
            for (i in rows.indices) {
                var x = 0
                val row = rows[i]
                val rowHeight = rowHeights[i]
                for (placeable in row) {
                    placeable.placeRelative(x, y)
                    x += placeable.width + mainSpacingPx
                }
                y += rowHeight + crossSpacingPx
            }
        }
    }
}
