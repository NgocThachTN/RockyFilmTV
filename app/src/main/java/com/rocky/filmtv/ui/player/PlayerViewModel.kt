package com.rocky.filmtv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.core.network.UiState
import com.rocky.filmtv.data.local.database.WatchHistoryEntity
import com.rocky.filmtv.data.remote.mapper.Episode
import com.rocky.filmtv.data.remote.mapper.MovieDetail
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class PlayerUiState(
    val movieDetail: MovieDetail? = null,
    val currentEpisode: Episode? = null,
    val currentEpisodeIndex: Int = -1,
    val isHasNext: Boolean = false,
    val isHasPrev: Boolean = false,
    val initialPosition: Long = 0L,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun loadEpisode(movieSlug: String, episodeIndex: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            try {
                // Fetch detail info
                val detail = repository.getChiTietPhim(movieSlug)
                val server = detail.episodes.firstOrNull() ?: throw Exception("Không có tập phim nào")
                val episodeList = server.episodes
                
                if (episodeIndex < 0 || episodeIndex >= episodeList.size) {
                    throw Exception("Không tìm thấy tập phim")
                }
                
                val currentEpisode = episodeList[episodeIndex]
                
                // Read from local watch history to resume playback if matches current episode
                val history = repository.getHistoryById(detail.id)
                val resumePosition = if (history != null && history.lastWatchedEpisodeSlug == currentEpisode.slug) {
                    history.lastWatchedPosition
                } else 0L

                _uiState.value = PlayerUiState(
                    movieDetail = detail,
                    currentEpisode = currentEpisode,
                    currentEpisodeIndex = episodeIndex,
                    isHasNext = episodeIndex < episodeList.lastIndex,
                    isHasPrev = episodeIndex > 0,
                    initialPosition = resumePosition,
                    isLoading = false
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading episode stream data")
                _uiState.value = PlayerUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = e.localizedMessage ?: "Không thể phát tập phim này"
                )
            }
        }
    }

    fun saveHistory(position: Long, duration: Long) {
        val state = _uiState.value
        val detail = state.movieDetail ?: return
        val episode = state.currentEpisode ?: return
        
        viewModelScope.launch {
            val history = WatchHistoryEntity(
                id = detail.id,
                name = detail.name,
                slug = detail.slug,
                posterUrl = detail.posterUrl.ifEmpty { detail.thumbUrl },
                lastWatchedEpisodeName = episode.name,
                lastWatchedEpisodeSlug = episode.slug,
                lastWatchedPosition = position,
                totalDuration = duration,
                timestamp = System.currentTimeMillis()
            )
            repository.insertHistory(history)
            Timber.d("Watch history successfully saved for movie ${detail.name} at position $position ms")
        }
    }
}
