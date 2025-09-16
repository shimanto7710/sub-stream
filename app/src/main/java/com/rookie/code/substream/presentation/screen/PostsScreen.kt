package com.rookie.code.substream.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.presentation.viewmodel.PostsViewModel
import com.rookie.code.substream.presentation.composables.VideoFeedScreen
import com.rookie.code.substream.presentation.composables.NoVideosContent
import com.rookie.code.substream.presentation.composables.isVideoPost
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostsScreen(
    subreddit: String,
    sorting: PostSortingEntity = PostSortingEntity.HOT,
    onBack: () -> Unit
) {
    val viewModel: PostsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(subreddit, sorting) {
        println("PostsScreen: Loading posts for subreddit: $subreddit with sorting: ${sorting.displayName}")
        viewModel.loadPosts(subreddit, sorting)
    }

    PostsScreenView(
        subreddit = subreddit,
        sorting = sorting,
        onBack = onBack,
        uiState = uiState,
        onRetry = { viewModel.loadPosts(subreddit) },
        onLoadMore = { viewModel.loadMorePosts(subreddit) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostsScreenView(
    subreddit: String,
    sorting: PostSortingEntity = PostSortingEntity.HOT,
    onBack: () -> Unit,
    uiState: PostsViewModel.PostsUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is PostsViewModel.PostsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PostsViewModel.PostsUiState.Error -> {
                ErrorContent(
                    message = (uiState as PostsViewModel.PostsUiState.Error).message,
                    onRetry = onRetry,
                    onBack = onBack
                )
            }
            is PostsViewModel.PostsUiState.Success -> {
                val allPosts = (uiState as PostsViewModel.PostsUiState.Success).posts
                val currentSorting = (uiState as PostsViewModel.PostsUiState.Success).sorting
                val videoPosts = allPosts.filter { isVideoPost(it) }
                
                if (videoPosts.isEmpty()) {
                    NoVideosContent(
                        message = "No videos found in r/$subreddit",
                        onRetry = onRetry,
                        onBack = onBack
                    )
                } else {
                    VideoFeedScreen(
                        posts = videoPosts,
                        subreddit = subreddit,
                        onBack = onBack,
                        onLoadMore = onLoadMore
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error loading posts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
            
            OutlinedButton(onClick = onBack) {
                Text("Back to Search")
            }
        }
    }
}// Preview composables


@Preview(showBackground = true)
@Composable
private fun PostsScreenViewLoadingPreview() {
    PostsScreenView(
        subreddit = "androiddev",
        sorting = PostSortingEntity.HOT,
        onBack = {},
        uiState = PostsViewModel.PostsUiState.Loading,
        onRetry = {},
        onLoadMore = {}
    )
}


