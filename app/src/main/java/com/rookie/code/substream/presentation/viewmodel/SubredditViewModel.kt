package com.rookie.code.substream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.domain.repository.SubredditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubredditUiState(
    val isLoading: Boolean = false,
    val subreddits: List<Subreddit> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

class SubredditViewModel(
    private val subredditRepository: SubredditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubredditUiState())
    val uiState: StateFlow<SubredditUiState> = _uiState.asStateFlow()

    init {
        loadPopularSubreddits()
    }

    fun loadPopularSubreddits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = subredditRepository.getPopularSubreddits(limit = 25)) {
                is com.rookie.code.substream.data.api.Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        subreddits = result.data,
                        error = null
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to load subreddits"
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun searchSubreddits(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                hasSearched = false,
                searchQuery = ""
            )
            loadPopularSubreddits()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                error = null,
                searchQuery = query,
                isSearching = true,
                hasSearched = true
            )
            
            when (val result = subredditRepository.searchSubreddits(query, limit = 25)) {
                is com.rookie.code.substream.data.api.Resource.Success -> {
                    println("SubredditViewModel: Search successful, found ${result.data.size} subreddits")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        subreddits = result.data,
                        error = null,
                        isSearching = false,
                        hasSearched = true  // Ensure hasSearched is set to true
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to search subreddits",
                        isSearching = false,
                        hasSearched = true  // Even on error, we've attempted a search
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        if (_uiState.value.searchQuery.isBlank()) {
            loadPopularSubreddits()
        } else {
            searchSubreddits(_uiState.value.searchQuery)
        }
    }
}
