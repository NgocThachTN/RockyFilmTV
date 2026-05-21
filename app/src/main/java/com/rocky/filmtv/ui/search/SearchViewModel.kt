package com.rocky.filmtv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<Movie>>>(UiState.Success(emptyList()))
    val searchState: StateFlow<UiState<List<Movie>>> = _searchState.asStateFlow()

    init {
        // Debounce search input to avoid overwhelming the OPhim API
        _query
            .debounce(600)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { q ->
                if (q.isEmpty()) {
                    _searchState.value = UiState.Success(emptyList())
                } else {
                    _searchState.value = UiState.Loading
                }
            }
            .filter { it.isNotEmpty() }
            .flatMapLatest { q ->
                flow {
                    try {
                        val results = repository.timKiemPhim(q, 1)
                        emit(UiState.Success(results))
                    } catch (e: Exception) {
                        Timber.e(e, "Search API failure for query: $q")
                        emit(UiState.Error(e.localizedMessage ?: "Lỗi tải kết quả tìm kiếm"))
                    }
                }
            }
            .onEach { _searchState.value = it }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun retrySearch() {
        val currentQuery = _query.value
        if (currentQuery.isNotEmpty()) {
            viewModelScope.launch {
                _searchState.value = UiState.Loading
                try {
                    val results = repository.timKiemPhim(currentQuery, 1)
                    _searchState.value = UiState.Success(results)
                } catch (e: Exception) {
                    _searchState.value = UiState.Error(e.localizedMessage ?: "Lỗi tải kết quả")
                }
            }
        }
    }
}
