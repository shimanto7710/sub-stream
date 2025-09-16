package com.rookie.code.substream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.domain.usecase.GetSubredditPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(
    private val getSubredditPostsUseCase: GetSubredditPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private var currentAfter: String? = null
    private var isLoadingMore = false
    private var currentSorting: PostSortingEntity = PostSortingEntity.HOT
    private var currentSubreddit: String = ""

    fun loadPosts(subreddit: String, sorting: PostSortingEntity = PostSortingEntity.HOT) {
        viewModelScope.launch {
            println("PostsViewModel: Loading posts for subreddit: $subreddit with sorting: ${sorting.displayName}")
            _uiState.value = PostsUiState.Loading
            currentAfter = null
            isLoadingMore = false
            currentSorting = sorting
            currentSubreddit = subreddit
            
            val result = getSubredditPostsUseCase(subreddit, sorting, 25, null)
            if (result.isSuccess) {
                val (posts, nextAfter) = result.getOrNull() ?: Pair(emptyList(), null)
                println("PostsViewModel: Successfully loaded ${posts.size} posts for subreddit: $subreddit")
                _uiState.value = PostsUiState.Success(posts, sorting)
                currentAfter = nextAfter
            } else {
                println("PostsViewModel: Error loading posts: ${result.exceptionOrNull()?.message}")
                _uiState.value = PostsUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun loadMorePosts(subreddit: String) {
        if (isLoadingMore || currentAfter == null) return
        
        viewModelScope.launch {
            isLoadingMore = true
            println("PostsViewModel: Loading more posts for subreddit: $subreddit with after: $currentAfter")
            
            val result = getSubredditPostsUseCase(subreddit, currentSorting, 25, currentAfter)
            if (result.isSuccess) {
                val (newPosts, nextAfter) = result.getOrNull() ?: Pair(emptyList(), null)
                println("PostsViewModel: Successfully loaded ${newPosts.size} more posts")

                val currentState = _uiState.value
                if (currentState is PostsUiState.Success) {
                    val allPosts = currentState.posts + newPosts
                    _uiState.value = PostsUiState.Success(allPosts, currentSorting)
                    currentAfter = nextAfter
                }
            } else {
                println("PostsViewModel: Error loading more posts: ${result.exceptionOrNull()?.message}")
                // Don't change UI state on pagination error, just log it
            }
            isLoadingMore = false
        }
    }

    sealed class PostsUiState {
        object Loading : PostsUiState()
        data class Success(val posts: List<RedditPostEntity>, val sorting: PostSortingEntity = PostSortingEntity.HOT) : PostsUiState()
        data class Error(val message: String) : PostsUiState()
    }
}

