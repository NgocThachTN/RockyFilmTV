package com.rocky.filmtv.ui.home.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.*
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.remote.mapper.Movie
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedBanner(
    activeMovie: Movie?,
    fallbackList: List<Movie>,
    onPlayClick: (Movie) -> Unit,
    onDetailClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDisplayMovie by remember { mutableStateOf<Movie?>(null) }

    // Sync currentDisplayMovie immediately when activeMovie changes (D-pad hover in row)
    LaunchedEffect(activeMovie) {
        if (activeMovie != null) {
            currentDisplayMovie = activeMovie
        }
    }

    // Auto slideshow transitions when user is idle (activeMovie stays the same)
    LaunchedEffect(activeMovie, fallbackList) {
        if (fallbackList.isNotEmpty()) {
            if (currentDisplayMovie == null) {
                currentDisplayMovie = activeMovie ?: fallbackList.first()
            }

            // Slide rotation loop
            while (true) {
                delay(7000) // Rotate every 7 seconds of inactivity
                val currentIdx = fallbackList.indexOf(currentDisplayMovie).coerceAtLeast(0)
                val nextIdx = (currentIdx + 1) % minOf(fallbackList.size, 5)
                currentDisplayMovie = fallbackList[nextIdx]
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(Color.Black)
    ) {
        Crossfade(
            targetState = currentDisplayMovie,
            label = "BannerCrossfade",
            modifier = Modifier.fillMaxSize()
        ) { movie ->
            if (movie != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    val imageRequest = remember(movie.thumbUrl, movie.posterUrl) {
                        ImageRequest.Builder(context)
                            .data(movie.thumbUrl.ifEmpty { movie.posterUrl })
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .size(width = 960, height = 540) // scale to 540p to optimize RAM
                            .precision(coil.size.Precision.AUTOMATIC)
                            .build()
                    }

                    // Backdrop Image
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = movie.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Cinematic Netflix Gradients (Darken edges for superb readability)
                    // Top dark overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Left-to-right dark horizontal mask (Netflix signature side shadow)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Bottom vertical fade-to-black mask
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

                    // Movie Details & Actions
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.55f)
                            .padding(start = 32.dp, bottom = 32.dp, top = 32.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Netflix-style Branded Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(PrimaryRed, shape = RoundedCornerShape(2.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "R",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = "P H I M  N Ổ I  B Ậ T",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Movie Title
                        Text(
                            text = movie.name,
                            color = Color.White,
                            fontSize = 32.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Netflix Metadata Subtitle Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "98% Trùng khớp",
                                color = Color(0xFF46D369),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${movie.year}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            // Age limit tag
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (movie.year < 2023) "T16" else "T18",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Ultra HD 4K badge
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "Ultra HD 4K",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Synopsis Descriptive Text
                        val description = remember(movie.id) {
                            when {
                                movie.type.contains("hoat-hinh") || movie.slug.contains("hoat-hinh") -> {
                                    "Hành trình kỳ ảo đầy màu sắc với đồ họa tuyệt đỉnh, mở ra thế giới giả tưởng ngoạn mục cùng câu chuyện cảm động chạm đến trái tim người xem."
                                }
                                movie.type.contains("bo") || movie.slug.contains("phim-bo") -> {
                                    "Siêu phẩm truyền hình bom tấn kịch tính với cốt truyện lớp lang, những màn đấu trí đỉnh cao và các cú ngoặt bất ngờ khiến bạn không thể rời mắt."
                                }
                                else -> {
                                    "Tác phẩm điện ảnh bom tấn ăn khách nhất mùa giải. Cốt truyện sâu sắc, kỹ xảo hoành tráng cùng diễn xuất đỉnh cao từ dàn sao hàng đầu."
                                }
                            }
                        }
                        Text(
                            text = description,
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Netflix-style Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { onPlayClick(movie) },
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black,
                                    focusedContainerColor = PrimaryRed,
                                    focusedContentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(text = "▶  Xem Ngay", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = { onDetailClick(movie) },
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    contentColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    focusedContentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(text = "ⓘ  Chi Tiết", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    // Slideshow Indicator Dots (Bottom Right)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 48.dp, bottom = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val slideCount = minOf(fallbackList.size, 5)
                        repeat(slideCount) { idx ->
                            val isActive = fallbackList.getOrNull(idx) == movie
                            Box(
                                modifier = Modifier
                                    .size(if (isActive) 10.dp else 6.dp)
                                    .background(
                                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }
                }
            } else {
                // Placeholder loading skeleton
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF141414)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Đang tải banner...", color = Color.Gray, fontSize = 16.sp)
                }
            }
        }
    }
}
