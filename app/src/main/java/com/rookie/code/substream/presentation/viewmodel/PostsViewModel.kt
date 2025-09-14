package com.rookie.code.substream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.model.RedditPost
import com.rookie.code.substream.domain.repository.RedditPostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(
    private val redditPostsRepository: RedditPostsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    fun loadPosts(subreddit: String) {
        viewModelScope.launch {
            println("PostsViewModel: Loading posts for subreddit: $subreddit")
            _uiState.value = PostsUiState.Loading
            
            when (val result = redditPostsRepository.getSubredditPosts(subreddit, 2)) {
                is Resource.Success -> {
                    println("PostsViewModel: Successfully loaded ${result.data.size} posts for subreddit: $subreddit")
                    _uiState.value = PostsUiState.Success(result.data)
                }
                is Resource.Error -> {
                    println("PostsViewModel: Error loading posts: ${result.exception?.message}")
                    _uiState.value = PostsUiState.Error(result.exception?.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    println("PostsViewModel: Still loading...")
                    _uiState.value = PostsUiState.Loading
                }
            }
        }
    }

    sealed class PostsUiState {
        object Loading : PostsUiState()
        data class Success(val posts: List<RedditPost>) : PostsUiState()
        data class Error(val message: String) : PostsUiState()
    }
}

