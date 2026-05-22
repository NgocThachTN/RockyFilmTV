package com.rocky.filmtv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.data.remote.mapper.StableMovieList
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeUiState(
    val phimMoi: StableMovieList = StableMovieList(),
    val phimLe: StableMovieList = StableMovieList(),
    val phimBo: StableMovieList = StableMovieList(),
    val hoatHinh: StableMovieList = StableMovieList(),
    val tvShows: StableMovieList = StableMovieList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _featuredMovie = MutableStateFlow<Movie?>(null)
    val featuredMovie: StateFlow<Movie?> = _featuredMovie.asStateFlow()

    private var featuredMovieJob: Job? = null

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                // Fetch datasets in parallel for faster load times on TV
                val phimMoiJob = repository.getPhimMoiCapNhat(1)
                val phimLeJob = repository.getPhimTheoTheLoai("phim-le", 1)
                val phimBoJob = repository.getPhimTheoTheLoai("phim-bo", 1)
                val hoatHinhJob = repository.getPhimTheoTheLoai("hoat-hinh", 1)
                val tvShowsJob = repository.getPhimTheoTheLoai("tv-shows", 1)

                _uiState.value = HomeUiState(
                    phimMoi = StableMovieList(phimMoiJob),
                    phimLe = StableMovieList(phimLeJob),
                    phimBo = StableMovieList(phimBoJob),
                    hoatHinh = StableMovieList(hoatHinhJob),
                    tvShows = StableMovieList(tvShowsJob),
                    isLoading = false
                )

                // Set default featured movie from the freshly updated list
                if (phimMoiJob.isNotEmpty()) {
                    _featuredMovie.value = phimMoiJob.first()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading OPhim TV Homepage data")
                _uiState.value = HomeUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = e.localizedMessage ?: "Đã xảy ra lỗi kết nối mạng"
                )
            }
        }
    }

    fun setFeaturedMovie(movie: Movie) {
        featuredMovieJob?.cancel()
        featuredMovieJob = viewModelScope.launch {
            delay(400) // Debounce D-pad scrolling to avoid rendering storm
            _featuredMovie.value = movie
        }
    }
}