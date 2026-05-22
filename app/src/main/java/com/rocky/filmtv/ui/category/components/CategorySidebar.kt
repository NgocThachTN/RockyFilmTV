package com.rocky.filmtv.ui.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*

val SIDEBAR_GENRES = listOf(
    Pair("hanh-dong", "Hành Động"),
    Pair("tinh-cam", "Tình Cảm"),
    Pair("hai-huoc", "Hài Hước"),
    Pair("co-trang", "Cổ Trang"),
    Pair("tam-ly", "Tâm Lý"),
    Pair("hinh-su", "Hình Sự"),
    Pair("chien-tranh", "Chiến Tranh"),
    Pair("the-thao", "Thể Thao"),
    Pair("vo-thuat", "Võ Thuật"),
    Pair("vien-tuong", "Viễn Tưởng"),
    Pair("phieu-luu", "Phiêu Lưu"),
    Pair("khoa-hoc", "Khoa Học"),
    Pair("kinh-di", "Kinh Dị"),
    Pair("am-nhac", "Âm Nhạc"),
    Pair("than-thoai", "Thần Thoại"),
    Pair("tai-lieu", "Tài Liệu"),
    Pair("gia-dinh", "Gia Đình"),
    Pair("chinh-kich", "Chính Kịch"),
    Pair("bi-an", "Bí Ẩn"),
    Pair("hoc-duong", "Học Đường"),
    Pair("kinh-dien", "Kinh Điển"),
    Pair("phim-18", "Phim 18+"),
    Pair("short-drama", "Short Drama"),
    Pair("phim-le", "Phim Lẻ"),
    Pair("phim-bo", "Phim Bộ")
)

val SIDEBAR_COUNTRIES = listOf(
    Pair("trung-quoc", "Trung Quốc"),
    Pair("han-quoc", "Hàn Quốc"),
    Pair("nhat-ban", "Nhật Bản"),
    Pair("thai-lan", "Thái Lan"),
    Pair("au-my", "Âu Mỹ"),
    Pair("dai-loan", "Đài Loan"),
    Pair("hong-kong", "Hồng Kông"),
    Pair("an-do", "Ấn Độ"),
    Pair("anh", "Anh Quốc"),
    Pair("phap", "Pháp"),
    Pair("canada", "Canada"),
    Pair("quoc-gia-khac", "Quốc Gia Khác"),
    Pair("duc", "Đức"),
    Pair("tay-ban-nha", "Tây Ban Nha"),
    Pair("tho-nhi-ky", "Thổ Nhĩ Kỳ"),
    Pair("ha-lan", "Hà Lan"),
    Pair("indonesia", "Indonesia"),
    Pair("nga", "Nga"),
    Pair("mexico", "Mexico"),
    Pair("ba-lan", "Ba Lan"),
    Pair("uc", "Úc"),
    Pair("thuy-dien", "Thụy Điển"),
    Pair("malaysia", "Malaysia"),
    Pair("brazil", "Brazil"),
    Pair("philippines", "Philippines"),
    Pair("bo-dao-nha", "Bồ Đào Nha"),
    Pair("y", "Ý"),
    Pair("dan-mach", "Đan Mạch"),
    Pair("uae", "UAE"),
    Pair("na-uy", "Na Uy"),
    Pair("thuy-si", "Thụy Sĩ"),
    Pair("chau-phi", "Châu Phi"),
    Pair("nam-phi", "Nam Phi"),
    Pair("ukraina", "Ukraina"),
    Pair("a-rap-xe-ut", "Ả Rập Xê Út"),
    Pair("bi", "Bỉ"),
    Pair("ireland", "Ireland"),
    Pair("colombia", "Colombia"),
    Pair("phan-lan", "Phần Lan"),
    Pair("viet-nam", "Việt Nam"),
    Pair("chile", "Chile"),
    Pair("hy-lap", "Hy Lạp"),
    Pair("nigeria", "Nigeria"),
    Pair("argentina", "Argentina"),
    Pair("singapore", "Singapore")
)

val FILTER_YEARS = listOf(
    "Tất Cả",
    "2026",
    "2025",
    "2024",
    "2023",
    "2022",
    "2021",
    "2020",
    "2019",
    "Trước 2019"
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategorySidebar(
    selectedCategoryType: String,
    selectedType: String,
    selectedYear: String,
    onCategorySelected: (String, String) -> Unit,
    onYearSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color(0xFF172A45).copy(alpha = 0.9f))
            .padding(horizontal = 12.dp, vertical = 20.dp)
    ) {
        // Back Button
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← Quay Lại",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Column 1 (Left): Thể Loại & Quốc Gia
            Column(
                modifier = Modifier
                    .width(146.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "THỂ LOẠI",
                    color = Color(0xFF00D2FF).copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                SIDEBAR_GENRES.forEach { (slug, name) ->
                    val isGenreSelected = (selectedCategoryType == "genre" || selectedCategoryType == "type") && selectedType == slug
                    FilterItem(
                        text = name,
                        isSelected = isGenreSelected,
                        onClick = {
                            onCategorySelected("genre", slug)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "QUỐC GIA",
                    color = Color(0xFF00D2FF).copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                SIDEBAR_COUNTRIES.forEach { (slug, name) ->
                    FilterItem(
                        text = name,
                        isSelected = selectedCategoryType == "country" && selectedType == slug,
                        onClick = {
                            onCategorySelected("country", slug)
                        }
                    )
                }
            }

            // Divider between columns
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.05f))
            )

            // Column 2 (Right): Năm Phát Hành
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "NĂM",
                    color = Color(0xFF00D2FF).copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                FILTER_YEARS.forEach { yr ->
                    FilterItem(
                        text = yr,
                        isSelected = selectedYear == yr,
                        onClick = {
                            onYearSelected(yr)
                        }
                    )
                }
            }
        }
    }
}
