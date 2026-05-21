package com.rocky.filmtv.core.network

/**
 * Standard State wrapper for UI data binding.
 */
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    
    data class Success<out T>(val data: T) : UiState<T>
    
    data class Error(val message: String) : UiState<Nothing>
}
