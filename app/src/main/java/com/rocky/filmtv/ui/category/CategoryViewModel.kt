package com.rocky.filmtv.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Movie>>> = _uiState.asStateFlow()

    private var currentType = ""
    private var currentPage = 1
    private val loadedMovies = mutableListOf<Movie>()

    fun loadCategory(type: String) {
        if (currentType == type) return // already loaded
        currentType = type
        currentPage = 1
        loadedMovies.clear()
        fetchMovies()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            if (currentPage == 1) {
                _uiState.value = UiState.Loading
            }
            try {
                val newMovies = repository.getPhimTheoTheLoai(currentType, currentPage)
                loadedMovies.addAll(newMovies)
                _uiState.value = UiState.Success(loadedMovies.toList())
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories for type: $currentType")
                if (currentPage == 1) {
                    _uiState.value = UiState.Error(e.localizedMessage ?: "Không thể tải danh sách phim")
                }
            }
        }
    }

    fun loadNextPage() {
        currentPage++
        fetchMovies()
    }
}
