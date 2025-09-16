package com.rookie.code.substream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookie.code.substream.common.constants.StringConstants
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.domain.usecase.GetPopularSubredditsUseCase
import com.rookie.code.substream.domain.usecase.SearchSubredditsUseCase
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
    private val getPopularSubredditsUseCase: GetPopularSubredditsUseCase,
    private val searchSubredditsUseCase: SearchSubredditsUseCase
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

            when (val result = getPopularSubredditsUseCase(limit = 25, after = null)) {
                is Resource.Success -> {
                    val (subreddits, nextAfter) = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        subreddits = subreddits,
                        error = null,
                        currentAfter = nextAfter,
                        canLoadMore = nextAfter != null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: SubredditViewModelConstants.FAILED_TO_LOAD_SUBREDDITS,
                        canLoadMore = false
                    )
                }
                is Resource.Loading -> {
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

            when (val result = searchSubredditsUseCase(query, limit = 25, after = null)) {
                is Resource.Success -> {
                    val (subreddits, nextAfter) = result.data
                    println("${SubredditViewModelConstants.LOG_TAG}: ${SubredditViewModelConstants.SEARCH_SUCCESSFUL.replace("{count}", subreddits.size.toString())}")
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
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: SubredditViewModelConstants.FAILED_TO_SEARCH_SUBREDDITS,
                        isSearching = false,
                        hasSearched = true,
                        canLoadMore = false
                    )
                }
                is Resource.Loading -> {
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
                getPopularSubredditsUseCase(limit = 25, after = currentState.currentAfter)
            } else {
                searchSubredditsUseCase(currentState.searchQuery, limit = 25, after = currentState.currentAfter)
            }

            when (result) {
                is Resource.Success -> {
                    val (newSubreddits, nextAfter) = result.data
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        subreddits = currentState.subreddits + newSubreddits,
                        currentAfter = nextAfter,
                        canLoadMore = nextAfter != null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        canLoadMore = false
                    )
                }
                is Resource.Loading -> {
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
