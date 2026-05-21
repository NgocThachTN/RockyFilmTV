package com.rocky.filmtv.ui.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.ui.home.components.MovieCard

private val SIDEBAR_GENRES = listOf(
    Pair("hanh-dong", "Hành Động"),
    Pair("vien-tuong", "Viễn Tưởng"),
    Pair("co-trang", "Cổ Trang"),
    Pair("tinh-cam", "Tình Cảm"),
    Pair("kinh-di", "Kinh Dị"),
    Pair("hai-huoc", "Hài Hước"),
    Pair("hinh-su", "Hình Sự"),
    Pair("vo-thuat", "Võ Thuật"),
    Pair("hoat-hinh", "Hoạt Hình"),
    Pair("tv-shows", "TV Shows"),
    Pair("phim-le", "Phim Lẻ"),
    Pair("phim-bo", "Phim Bộ")
)

private val SIDEBAR_COUNTRIES = listOf(
    Pair("han-quoc", "Hàn Quốc"),
    Pair("trung-quoc", "Trung Quốc"),
    Pair("au-my", "Âu Mỹ"),
    Pair("nhat-ban", "Nhật Bản"),
    Pair("viet-nam", "Việt Nam"),
    Pair("thai-lan", "Thái Lan"),
    Pair("dai-loan", "Đài Loan"),
    Pair("an-do", "Ấn Độ"),
    Pair("hong-kong", "Hồng Kông"),
    Pair("phap", "Pháp"),
    Pair("anh", "Anh Quốc"),
    Pair("canada", "Canada")
)

private val FILTER_YEARS = listOf(
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
fun FilterItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(
            containerColor = if (isSelected) Color(0xFF00D2FF).copy(alpha = 0.15f) else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF00D2FF) else Color.White,
            focusedContainerColor = Color.White,
            focusedContentColor = Color.Black
        ),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (isSelected) Color(0xFF00D2FF) else Color.Transparent
                )
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color(0xFF00D2FF))
            )
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(8.dp)),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF00D2FF), shape = RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryType: String,
    type: String,
    title: String,
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var selectedCategoryType by remember { mutableStateOf(categoryType) }
    var selectedType by remember { mutableStateOf(type) }
    var selectedYear by remember { mutableStateOf("Tất Cả") }

    LaunchedEffect(selectedCategoryType, selectedType) {
        viewModel.loadCategory(selectedCategoryType, selectedType)
    }

    val activeItemName = when (selectedCategoryType) {
        "country" -> SIDEBAR_COUNTRIES.find { it.first == selectedType }?.second ?: title
        else -> SIDEBAR_GENRES.find { it.first == selectedType }?.second ?: title
    }
    val displayTitle = if (selectedYear == "Tất Cả") activeItemName else "$activeItemName - Năm $selectedYear"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Sidebar Panel with two side-by-side columns (Thể loại/Quốc gia on left, Năm on right)
            Column(
                modifier = Modifier
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
                            FilterItem(
                                text = name,
                                isSelected = selectedCategoryType == "genre" && selectedType == slug,
                                onClick = {
                                    selectedCategoryType = "genre"
                                    selectedType = slug
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
                                    selectedCategoryType = "country"
                                    selectedType = slug
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
                                    selectedYear = yr
                                }
                            )
                        }
                    }
                }
            }

            // Divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.1f))
            )

            // Right adaptive movie grid
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Header (Display dynamic title)
                Text(
                    text = displayTitle,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Category list Grid
                when (val uiState = state) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00D2FF))
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.message,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Button(onClick = { viewModel.fetchMovies() }) {
                                Text("Thử Lại")
                            }
                        }
                    }
                    is UiState.Success -> {
                        val movies = uiState.data
                        val filteredMovies = remember(movies, selectedYear) {
                            movies.filter { movie ->
                                when (selectedYear) {
                                    "Tất Cả" -> true
                                    "Trước 2019" -> movie.year < 2019
                                    else -> movie.year.toString() == selectedYear
                                }
                            }
                        }

                        if (filteredMovies.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không tìm thấy phim nào khớp với bộ lọc năm $selectedYear",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(filteredMovies) { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onMovieClick = { onNavigateToDetail(it.slug) }
                                    )
                                }

                                // D-pad Load More Button at bottom of the grid
                                item(span = { GridItemSpan(4) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            onClick = { viewModel.loadNextPage() },
                                            colors = ButtonDefaults.colors(
                                                containerColor = Color(0xFF2C2C2E),
                                                contentColor = Color.White,
                                                focusedContainerColor = Color.White,
                                                focusedContentColor = Color.Black
                                            )
                                        ) {
                                            Text("Xem Thêm Phim", fontWeight = FontWeight.Bold)
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
}
