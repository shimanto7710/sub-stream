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
    val hasSearched: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val currentAfter: String? = null
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
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                error = null,
                currentAfter = null,
                canLoadMore = true
            )
            
            when (val result = subredditRepository.getPopularSubreddits(limit = 25, after = null)) {
                is com.rookie.code.substream.data.api.Resource.Success -> {
                    val (subreddits, nextAfter) = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        subreddits = subreddits,
                        error = null,
                        currentAfter = nextAfter,
                        canLoadMore = nextAfter != null
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to load subreddits",
                        canLoadMore = false
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
                searchQuery = "",
                currentAfter = null,
                canLoadMore = true
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
                hasSearched = true,
                currentAfter = null,
                canLoadMore = true
            )
            
            when (val result = subredditRepository.searchSubreddits(query, limit = 25, after = null)) {
                is com.rookie.code.substream.data.api.Resource.Success -> {
                    val (subreddits, nextAfter) = result.data
                    println("SubredditViewModel: Search successful, found ${subreddits.size} subreddits")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        subreddits = subreddits,
                        error = null,
                        isSearching = false,
                        hasSearched = true,
                        currentAfter = nextAfter,
                        canLoadMore = nextAfter != null
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to search subreddits",
                        isSearching = false,
                        hasSearched = true,
                        canLoadMore = false
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun loadMoreSubreddits() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.canLoadMore || currentState.currentAfter == null) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)
            
            val result = if (currentState.searchQuery.isBlank()) {
                subredditRepository.getPopularSubreddits(limit = 25, after = currentState.currentAfter)
            } else {
                subredditRepository.searchSubreddits(currentState.searchQuery, limit = 25, after = currentState.currentAfter)
            }
            
            when (result) {
                is com.rookie.code.substream.data.api.Resource.Success -> {
                    val (newSubreddits, nextAfter) = result.data
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        subreddits = currentState.subreddits + newSubreddits,
                        currentAfter = nextAfter,
                        canLoadMore = nextAfter != null
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Error -> {
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        canLoadMore = false
                    )
                }
                is com.rookie.code.substream.data.api.Resource.Loading -> {
                    _uiState.value = currentState.copy(isLoadingMore = true)
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
