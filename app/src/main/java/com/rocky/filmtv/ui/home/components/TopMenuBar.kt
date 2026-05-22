package com.rocky.filmtv.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopMenuBar(
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGenreList: () -> Unit,
    onNavigateToCategory: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stunning Light Sea Blue Circular Play Logo
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFF00F0FF), Color(0xFF0072FF))
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(14.dp)) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.28f, size.height * 0.18f)
                        lineTo(size.width * 0.82f, size.height * 0.5f)
                        lineTo(size.width * 0.28f, size.height * 0.82f)
                        close()
                    }
                    drawPath(path = path, color = Color.White)
                }
            }

            // Typography brand text
            Row {
                Text(
                    text = "ROCKY FILM ",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                )
                Text(
                    text = "TV",
                    color = PrimaryRed,
                    fontSize = 19.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                )
            }
        }

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
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Tìm Kiếm",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = onNavigateToFavorite,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Yêu Thích",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = onNavigateToHistory,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Lịch Sử Xem",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = onNavigateToGenreList,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Thể Loại",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = { onNavigateToCategory("type", "phim-bo", "Phim Bộ") },
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Phim Bộ",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Button(
                onClick = { onNavigateToCategory("type", "phim-le", "Phim Lẻ") },
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Phim Lẻ",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
