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
    GenreItem("tinh-cam", "Tình Cảm", Color(0xFFEC008C), Color(0xFFFC6767)),
    GenreItem("hai-huoc", "Hài Hước", Color(0xFF11998E), Color(0xFF38EF7D)),
    GenreItem("co-trang", "Cổ Trang", Color(0xFFF12711), Color(0xFFF5AF19)),
    GenreItem("tam-ly", "Tâm Lý", Color(0xFF00C6FF), Color(0xFF0072FF)),
    GenreItem("hinh-su", "Hình Sự", Color(0xFF3A7BD5), Color(0xFF3A6073)),
    GenreItem("chien-tranh", "Chiến Tranh", Color(0xFF4B6CB7), Color(0xFF182848)),
    GenreItem("the-thao", "Thể Thao", Color(0xFF4CA1AF), Color(0xFF2C3E50)),
    GenreItem("vo-thuat", "Võ Thuật", Color(0xFFED213A), Color(0xFF93291E)),
    GenreItem("vien-tuong", "Viễn Tưởng", Color(0xFF00B4DB), Color(0xFF0083B0)),
    GenreItem("phieu-luu", "Phiêu Lưu", Color(0xFFF857A6), Color(0xFFFF5858)),
    GenreItem("khoa-hoc", "Khoa Học", Color(0xFF2193B0), Color(0xFF6DD5ED)),
    GenreItem("kinh-di", "Kinh Dị", Color(0xFF141E30), Color(0xFF243B55)),
    GenreItem("am-nhac", "Âm Nhạc", Color(0xFF8E2DE2), Color(0xFF4A00E0)),
    GenreItem("than-thoai", "Thần Thoại", Color(0xFF4568DC), Color(0xFFB06AB3)),
    GenreItem("tai-lieu", "Tài Liệu", Color(0xFF56AB2F), Color(0xFFA8E063)),
    GenreItem("gia-dinh", "Gia Đình", Color(0xFFFF9966), Color(0xFFFF5E62)),
    GenreItem("chinh-kich", "Chính Kịch", Color(0xFFD3A25D), Color(0xFF2C3E50)),
    GenreItem("bi-an", "Bí Ẩn", Color(0xFF3F2B96), Color(0xFFA8C0FF)),
    GenreItem("hoc-duong", "Học Đường", Color(0xFF85D8CE), Color(0xFF085078)),
    GenreItem("kinh-dien", "Kinh Điển", Color(0xFFC39B62), Color(0xFF805A29)),
    GenreItem("phim-18", "Phim 18+", Color(0xFF800914), Color(0xFFD50914)),
    GenreItem("short-drama", "Short Drama", Color(0xFFF07865), Color(0xFFF5AF19)),
    GenreItem("phim-le", "Phim Lẻ", Color(0xFFC04848), Color(0xFF480048)),
    GenreItem("phim-bo", "Phim Bộ", Color(0xFF5C258D), Color(0xFF4389A2))
)

val COUNTRIES = listOf(
    GenreItem("trung-quoc", "Trung Quốc", Color(0xFFED213A), Color(0xFFF5AF19)),
    GenreItem("han-quoc", "Hàn Quốc", Color(0xFFEC008C), Color(0xFF8E2DE2)),
    GenreItem("nhat-ban", "Nhật Bản", Color(0xFFE96443), Color(0xFF904E95)),
    GenreItem("thai-lan", "Thái Lan", Color(0xFFF12711), Color(0xFFF5AF19)),
    GenreItem("au-my", "Âu Mỹ", Color(0xFF00C6FF), Color(0xFF0072FF)),
    GenreItem("dai-loan", "Đài Loan", Color(0xFFF857A6), Color(0xFFFF5858)),
    GenreItem("hong-kong", "Hồng Kông", Color(0xFF4568DC), Color(0xFFB06AB3)),
    GenreItem("an-do", "Ấn Độ", Color(0xFFF857A6), Color(0xFFF5AF19)),
    GenreItem("anh", "Anh Quốc", Color(0xFF3A7BD5), Color(0xFF0072FF)),
    GenreItem("phap", "Pháp", Color(0xFF8E2DE2), Color(0xFFFC6767)),
    GenreItem("canada", "Canada", Color(0xFFD31027), Color(0xFFEA1027)),
    GenreItem("quoc-gia-khac", "Quốc Gia Khác", Color(0xFF757F9A), Color(0xFFD7DDE8)),
    GenreItem("duc", "Đức", Color(0xFF2C3E50), Color(0xFFFD746C)),
    GenreItem("tay-ban-nha", "Tây Ban Nha", Color(0xFFF12711), Color(0xFFFD746C)),
    GenreItem("tho-nhi-ky", "Thổ Nhĩ Kỳ", Color(0xFF11998E), Color(0xFF38EF7D)),
    GenreItem("ha-lan", "Hà Lan", Color(0xFFFF8C00), Color(0xFFFF4500)),
    GenreItem("indonesia", "Indonesia", Color(0xFFE55D87), Color(0xFF5FC3E4)),
    GenreItem("nga", "Nga", Color(0xFF2193B0), Color(0xFF6DD5ED)),
    GenreItem("mexico", "Mexico", Color(0xFF134E5E), Color(0xFF71B280)),
    GenreItem("ba-lan", "Ba Lan", Color(0xFFC04848), Color(0xFFF857A6)),
    GenreItem("uc", "Úc", Color(0xFF56AB2F), Color(0xFF38EF7D)),
    GenreItem("thuy-dien", "Thụy Điển", Color(0xFF00B4DB), Color(0xFF0083B0)),
    GenreItem("malaysia", "Malaysia", Color(0xFF4CA1AF), Color(0xFF2C3E50)),
    GenreItem("brazil", "Brazil", Color(0xFF56AB2F), Color(0xFFF5AF19)),
    GenreItem("philippines", "Philippines", Color(0xFF00C6FF), Color(0xFF0072FF)),
    GenreItem("bo-dao-nha", "Bồ Đào Nha", Color(0xFF56AB2F), Color(0xFFED213A)),
    GenreItem("y", "Ý", Color(0xFF8A2387), Color(0xFFE94057)),
    GenreItem("dan-mach", "Đan Mạch", Color(0xFFED213A), Color(0xFFE50914)),
    GenreItem("uae", "UAE", Color(0xFFD1913C), Color(0xFF2C3E50)),
    GenreItem("na-uy", "Na Uy", Color(0xFF141E30), Color(0xFF243B55)),
    GenreItem("thuy-si", "Thụy Sĩ", Color(0xFFD7DDE8), Color(0xFF757F9A)),
    GenreItem("chau-phi", "Châu Phi", Color(0xFFE65C00), Color(0xFFF9D423)),
    GenreItem("nam-phi", "Nam Phi", Color(0xFF11998E), Color(0xFFF5AF19)),
    GenreItem("ukraina", "Ukraina", Color(0xFF00C6FF), Color(0xFFF5AF19)),
    GenreItem("a-rap-xe-ut", "Ả Rập Xê Út", Color(0xFF11998E), Color(0xFF128C7E)),
    GenreItem("bi", "Bỉ", Color(0xFF3E5151), Color(0xFFDECBA4)),
    GenreItem("ireland", "Ireland", Color(0xFF00CDAC), Color(0xFF02AAB0)),
    GenreItem("colombia", "Colombia", Color(0xFFFF512F), Color(0xFFDD2476)),
    GenreItem("phan-lan", "Phần Lan", Color(0xFF1D976C), Color(0xFF93F9B9)),
    GenreItem("viet-nam", "Việt Nam", Color(0xFFE50914), Color(0xFFEC008C)),
    GenreItem("chile", "Chile", Color(0xFFB06AB3), Color(0xFF4568DC)),
    GenreItem("hy-lap", "Hy Lạp", Color(0xFF00D2FF), Color(0xFF3A7BD5)),
    GenreItem("nigeria", "Nigeria", Color(0xFF00A86B), Color(0xFF005C53)),
    GenreItem("argentina", "Argentina", Color(0xFF00D2FF), Color(0xFFD7DDE8)),
    GenreItem("singapore", "Singapore", Color(0xFFED213A), Color(0xFFFF9966))
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
