package com.rocky.filmtv.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rocky.filmtv.core.theme.DarkBackground
import com.rocky.filmtv.ui.category.components.CategoryMovieGrid
import com.rocky.filmtv.ui.category.components.CategorySidebar
import com.rocky.filmtv.ui.category.components.SIDEBAR_COUNTRIES
import com.rocky.filmtv.ui.category.components.SIDEBAR_GENRES

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
            // Left Sidebar Panel
            CategorySidebar(
                selectedCategoryType = selectedCategoryType,
                selectedType = selectedType,
                selectedYear = selectedYear,
                onCategorySelected = { catType, slug ->
                    selectedCategoryType = catType
                    selectedType = slug
                },
                onYearSelected = { yr ->
                    selectedYear = yr
                },
                onBackClick = onBackClick
            )

            // Divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.1f))
            )

            // Right adaptive movie grid
            CategoryMovieGrid(
                displayTitle = displayTitle,
                uiState = state,
                selectedYear = selectedYear,
                onMovieClick = { movie -> onNavigateToDetail(movie.slug) },
                onRetryClick = { viewModel.fetchMovies() },
                onLoadNextPage = { viewModel.loadNextPage() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
