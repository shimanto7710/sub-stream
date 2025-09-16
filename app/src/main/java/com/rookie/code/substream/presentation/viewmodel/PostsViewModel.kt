package com.rookie.code.substream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.model.RedditPost
import com.rookie.code.substream.data.model.PostSorting
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
    
    private var currentAfter: String? = null
    private var isLoadingMore = false
    private var currentSorting: PostSorting = PostSorting.HOT
    private var currentSubreddit: String = ""

    fun loadPosts(subreddit: String, sorting: PostSorting = PostSorting.HOT) {
        viewModelScope.launch {
            println("PostsViewModel: Loading posts for subreddit: $subreddit with sorting: ${sorting.displayName}")
            _uiState.value = PostsUiState.Loading
            currentAfter = null
            isLoadingMore = false
            currentSorting = sorting
            currentSubreddit = subreddit
            
            when (val result = redditPostsRepository.getSubredditPosts(subreddit, sorting, 25, null)) {
                is Resource.Success -> {
                    val (posts, nextAfter) = result.data
                    println("PostsViewModel: Successfully loaded ${posts.size} posts for subreddit: $subreddit")
                    _uiState.value = PostsUiState.Success(posts, sorting)
                    currentAfter = nextAfter
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
    
    fun loadMorePosts(subreddit: String) {
        if (isLoadingMore || currentAfter == null) return
        
        viewModelScope.launch {
            isLoadingMore = true
            println("PostsViewModel: Loading more posts for subreddit: $subreddit with after: $currentAfter")
            
            when (val result = redditPostsRepository.getSubredditPosts(subreddit, currentSorting, 25, currentAfter)) {
                is Resource.Success -> {
                    val (newPosts, nextAfter) = result.data
                    println("PostsViewModel: Successfully loaded ${newPosts.size} more posts")
                    
                    val currentState = _uiState.value
                    if (currentState is PostsUiState.Success) {
                        val allPosts = currentState.posts + newPosts
                        _uiState.value = PostsUiState.Success(allPosts, currentSorting)
                        currentAfter = nextAfter
                    }
                }
                is Resource.Error -> {
                    println("PostsViewModel: Error loading more posts: ${result.exception?.message}")
                    // Don't change UI state on pagination error, just log it
                }
                is Resource.Loading -> {
                    println("PostsViewModel: Still loading more posts...")
                }
            }
            isLoadingMore = false
        }
    }

    fun changeSorting(sorting: PostSorting) {
        if (currentSubreddit.isNotEmpty()) {
            loadPosts(currentSubreddit, sorting)
        }
    }

    sealed class PostsUiState {
        object Loading : PostsUiState()
        data class Success(val posts: List<RedditPost>, val sorting: PostSorting = PostSorting.HOT) : PostsUiState()
        data class Error(val message: String) : PostsUiState()
    }
}

