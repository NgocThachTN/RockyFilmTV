package com.rocky.filmtv.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.data.local.database.WatchHistoryEntity
import com.rocky.filmtv.data.remote.mapper.MovieDetail
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetail>> = _uiState.asStateFlow()

    private val _movieSlug = MutableStateFlow<String?>(null)

    // Flow that listens to slug changes and returns favorite state
    val isFavorite: StateFlow<Boolean> = _movieSlug
        .filterNotNull()
        .flatMapLatest { slug ->
            // Use the slug as the unique favorite ID or resolve ID when detail finishes loading
            // Room isFavorite is keyed by movie ID. We can map the detail's ID once loaded.
            _movieDetail.filterNotNull().flatMapLatest { detail ->
                repository.isFavorite(detail.id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Flow that listens to slug changes and returns the last watch history (if any)
    val watchHistory: StateFlow<WatchHistoryEntity?> = _movieSlug
        .filterNotNull()
        .flatMapLatest { slug ->
            _movieDetail.filterNotNull().map { detail ->
                repository.getHistoryById(detail.id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)

    fun loadMovieDetail(slug: String) {
        _movieSlug.value = slug
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val detail = repository.getChiTietPhim(slug)
                _movieDetail.value = detail
                _uiState.value = UiState.Success(detail)
            } catch (e: Exception) {
                Timber.e(e, "Error loading movie details for slug: $slug")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Không thể tải thông tin chi tiết phim")
            }
        }
    }

    fun toggleFavorite() {
        val detail = _movieDetail.value ?: return
        viewModelScope.launch {
            val currentlyFavorite = isFavorite.value
            if (currentlyFavorite) {
                repository.deleteFavorite(detail.id)
            } else {
                repository.insertFavorite(detail)
            }
        }
    }
}
