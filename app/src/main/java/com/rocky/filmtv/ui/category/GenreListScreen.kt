package com.rocky.filmtv.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.core.utils.tvFocusScale

data class GenreItem(
    val slug: String,
    val title: String,
    val startColor: Color,
    val endColor: Color
)

val GENRES = listOf(
    GenreItem("hanh-dong", "Hành Động", Color(0xFFE50914), Color(0xFFFF5F6D)),
    GenreItem("vien-tuong", "Viễn Tưởng", Color(0xFF00C6FF), Color(0xFF0072FF)),
    GenreItem("co-trang", "Cổ Trang", Color(0xFFF12711), Color(0xFFF5AF19)),
    GenreItem("tinh-cam", "Tình Cảm", Color(0xFFEC008C), Color(0xFFFC6767)),
    GenreItem("kinh-di", "Kinh Dị", Color(0xFF141E30), Color(0xFF243B55)),
    GenreItem("hai-huoc", "Hài Hước", Color(0xFF11998E), Color(0xFF38EF7D)),
    GenreItem("hinh-su", "Hình Sự", Color(0xFF3A7BD5), Color(0xFF3A6073)),
    GenreItem("vo-thuat", "Võ Thuật", Color(0xFFED213A), Color(0xFF93291E)),
    GenreItem("hoat-hinh", "Hoạt Hình", Color(0xFF8E2DE2), Color(0xFF4A00E0)),
    GenreItem("tv-shows", "TV Shows", Color(0xFF00B4DB), Color(0xFF0083B0)),
    GenreItem("phim-le", "Phim Lẻ", Color(0xFFF857A6), Color(0xFFFF5858)),
    GenreItem("phim-bo", "Phim Bộ", Color(0xFF4568DC), Color(0xFFB06AB3))
)

val COUNTRIES = listOf(
    GenreItem("han-quoc", "Hàn Quốc", Color(0xFF0F2027), Color(0xFF203A43)),
    GenreItem("trung-quoc", "Trung Quốc", Color(0xFFF12711), Color(0xFFF5AF19)),
    GenreItem("au-my", "Âu Mỹ", Color(0xFF2193B0), Color(0xFF6DD5ED)),
    GenreItem("nhat-ban", "Nhật Bản", Color(0xFFE96443), Color(0xFF904E95)),
    GenreItem("viet-nam", "Việt Nam", Color(0xFFED213A), Color(0xFF93291E)),
    GenreItem("thai-lan", "Thái Lan", Color(0xFF11998E), Color(0xFF38EF7D)),
    GenreItem("dai-loan", "Đài Loan", Color(0xFF3A7BD5), Color(0xFF3A6073)),
    GenreItem("an-do", "Ấn Độ", Color(0xFF8E2DE2), Color(0xFF4A00E0)),
    GenreItem("hong-kong", "Hồng Kông", Color(0xFFF857A6), Color(0xFFFF5858)),
    GenreItem("phap", "Pháp", Color(0xFF00B4DB), Color(0xFF0083B0)),
    GenreItem("anh", "Anh Quốc", Color(0xFF4568DC), Color(0xFFB06AB3)),
    GenreItem("canada", "Canada", Color(0xFFE65C00), Color(0xFFF9D423))
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GenreListScreen(
    onNavigateToCategory: (String, String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Genres, 1 = Countries

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Khám Phá Phim",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tabs Selector Row (Genres vs Countries)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    colors = ButtonDefaults.colors(
                        containerColor = if (selectedTab == 0) PrimaryRed else Color(0xFF2C2C2E),
                        contentColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Thể Loại Phim",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.colors(
                        containerColor = if (selectedTab == 1) PrimaryRed else Color(0xFF2C2C2E),
                        contentColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Bộ Lọc Quốc Gia",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            // Grid of categories / countries depending on active tab
            val currentList = if (selectedTab == 0) GENRES else COUNTRIES
            val currentCategoryType = if (selectedTab == 0) "genre" else "country"

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(currentList) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .tvFocusScale(
                                scaleFactor = 1.08f,
                                cornerRadius = 12.dp,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderWidth = 3.dp
                            )
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(item.startColor, item.endColor)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onNavigateToCategory(currentCategoryType, item.slug, item.title)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
