package com.rocky.filmtv.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocky.filmtv.data.local.database.WatchHistoryEntity
import com.rocky.filmtv.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val historyItems: StateFlow<List<WatchHistoryEntity>> = repository.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }
}
