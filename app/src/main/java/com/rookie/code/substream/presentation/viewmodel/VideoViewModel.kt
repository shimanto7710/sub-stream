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

data class VideoUiState(
    val posts: List<RedditPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFullscreen: Boolean = false,
    val currentVideoPost: RedditPost? = null
)

class VideoViewModel(
    private val redditPostsRepository: RedditPostsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun loadPosts(subreddit: String, limit: Int = 25) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = redditPostsRepository.getSubredditPosts(subreddit, limit)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        posts = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun playVideo(post: RedditPost) {
        _uiState.value = _uiState.value.copy(
            isFullscreen = true,
            currentVideoPost = post
        )
    }

    fun exitFullscreen() {
        _uiState.value = _uiState.value.copy(
            isFullscreen = false,
            currentVideoPost = null
        )
    }

    fun playNextVideo() {
        val currentPost = _uiState.value.currentVideoPost
        val posts = _uiState.value.posts
        
        if (currentPost != null && posts.isNotEmpty()) {
            val currentIndex = posts.indexOf(currentPost)
            val nextIndex = (currentIndex + 1) % posts.size
            val nextPost = posts[nextIndex]
            
            _uiState.value = _uiState.value.copy(currentVideoPost = nextPost)
        }
    }
}
