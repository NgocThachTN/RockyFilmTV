package com.rocky.filmtv.ui.history

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.local.database.WatchHistoryEntity

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val items by viewModel.historyItems.collectAsState()

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
                    text = "Lịch Sử Xem Phim",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // History Grid
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lịch sử xem trống. Hãy chọn một bộ phim và bắt đầu thưởng thức!",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4), // slightly larger cards
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(items) { history ->
                        HistoryCard(
                            history = history,
                            onCardClick = { onNavigateToDetail(history.slug) },
                            onDeleteClick = { viewModel.deleteHistoryItem(history.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HistoryCard(
    history: WatchHistoryEntity,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")

    val progress = if (history.totalDuration > 0) {
        history.lastWatchedPosition.toFloat() / history.totalDuration.toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .width(200.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Thumbnail Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            AsyncImage(
                model = history.posterUrl,
                contentDescription = history.name,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray, RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            // Dynamic Watch completion overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Column {
                    Text(
                        text = "Tập: ${history.lastWatchedEpisodeName}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    LinearProgressIndicator(
                        progress = progress,
                        color = PrimaryRed,
                        trackColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title info
        Text(
            text = history.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Actions underneath
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCardClick,
                colors = ButtonDefaults.colors(
                    containerColor = PrimaryRed,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Xem", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.colors(
                    containerColor = Color(0xFF2C2C2E),
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Xóa", fontSize = 11.sp)
            }
        }
    }
}
