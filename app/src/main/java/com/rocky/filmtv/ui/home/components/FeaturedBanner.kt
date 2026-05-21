package com.rocky.filmtv.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.remote.mapper.Movie

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedBanner(
    movie: Movie?,
    onPlayClick: (Movie) -> Unit,
    onDetailClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(Color.Black)
    ) {
        if (movie != null) {
            // Backdrop Image
            AsyncImage(
                model = movie.thumbUrl.ifEmpty { movie.posterUrl },
                contentDescription = movie.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Cinematic Gradients (Darken edges for text readability)
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Movie Details & Actions
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f) // Take left half of the screen
                    .padding(start = 32.dp, bottom = 32.dp, top = 32.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Focus indicator banner tag
                Box(
                    modifier = Modifier
                        .background(PrimaryRed, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NỔI BẬT",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = movie.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${movie.originName} • ${movie.year} • ${movie.type.uppercase()}",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions Layout
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onPlayClick(movie) },
                        colors = ButtonDefaults.colors(
                            containerColor = PrimaryRed,
                            contentColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedContentColor = Color.Black
                        )
                    ) {
                        Text(text = "Xem Ngay", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onDetailClick(movie) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedContentColor = Color.Black
                        )
                    ) {
                        Text(text = "Chi Tiết", fontWeight = FontWeight.Bold)
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
