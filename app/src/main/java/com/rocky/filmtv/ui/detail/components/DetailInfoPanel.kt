package com.rocky.filmtv.ui.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.local.database.WatchHistoryEntity
import com.rocky.filmtv.data.remote.mapper.MovieDetail
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailInfoPanel(
    detail: MovieDetail,
    isFavorite: Boolean,
    watchHistory: WatchHistoryEntity?,
    onNavigateToPlayer: (String, Int) -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            delay(200)
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignore
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = detail.name,
            fontSize = 36.sp,
            lineHeight = 44.sp,
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
                modifier = Modifier.focusRequester(focusRequester),
                colors = ButtonDefaults.colors(
                    containerColor = PrimaryRed,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                val btnText = if (watchHistory != null) {
                    "Tiếp Tục Xem (${watchHistory.lastWatchedEpisodeName})"
                } else "Xem Ngay"
                Text(
                    text = btnText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = onToggleFavorite,
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
