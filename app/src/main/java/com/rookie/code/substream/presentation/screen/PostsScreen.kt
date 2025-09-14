package com.rookie.code.substream.presentation.screen

import android.content.Context
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rookie.code.substream.data.model.RedditPost
import com.rookie.code.substream.presentation.viewmodel.PostsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    subreddit: String,
    onBack: () -> Unit,
    viewModel: PostsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(subreddit) {
        viewModel.loadPosts(subreddit)
    }

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
                onRetry = { viewModel.loadPosts(subreddit) }
            )
        }
        is PostsViewModel.PostsUiState.Success -> {
            val allPosts = (uiState as PostsViewModel.PostsUiState.Success).posts
            val videoPosts = allPosts.filter { isVideoPost(it) }
            
            if (videoPosts.isEmpty()) {
                NoVideosContent(
                    message = "No videos found in r/$subreddit",
                    onRetry = { viewModel.loadPosts(subreddit) }
                )
            } else {
                VideoFeedScreen(
                    posts = videoPosts,
                    subreddit = subreddit,
                    onBack = onBack
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoFeedScreen(
    posts: List<RedditPost>,
    subreddit: String,
    onBack: () -> Unit
) {
    var currentVideoIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { posts.size }
    )
    
    // Sync pager state with current video index
    LaunchedEffect(pagerState.currentPage) {
        currentVideoIndex = pagerState.currentPage
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Vertical scrolling videos using VerticalPager
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            FullScreenVideoPlayer(
                post = posts[page],
                onClose = onBack,
                showControls = true,
                isCurrentVideo = page == currentVideoIndex,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Top overlay with subreddit info and video counter
        VideoFeedOverlay(
            subreddit = subreddit,
            currentIndex = currentVideoIndex + 1,
            totalVideos = posts.size,
            onBack = onBack
        )
        
        // Swipe instruction hint
        if (currentVideoIndex < posts.size - 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            ) {
                Text(
                    text = "↑ Swipe up for next video",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}


@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
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
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun NoVideosContent(
    message: String,
    onRetry: () -> Unit,
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
            text = "📹 No Videos Found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Try a different subreddit that might have more video content!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Refresh")
        }
    }
}

// Video-related composables and functions

@Composable
private fun FullScreenVideoPlayer(
    post: RedditPost,
    onClose: () -> Unit,
    showControls: Boolean = true,
    isCurrentVideo: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(isCurrentVideo) }
    var isMuted by remember { mutableStateOf(false) }
    var showVideoControls by remember { mutableStateOf(showControls) }
    
    // Auto-hide controls after 3 seconds
    LaunchedEffect(showVideoControls) {
        if (showVideoControls) {
            kotlinx.coroutines.delay(3000)
            showVideoControls = false
        }
    }
    
    // Pause video when not current
    LaunchedEffect(isCurrentVideo) {
        isPlaying = isCurrentVideo
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showVideoControls = !showVideoControls }
    ) {
        // Video Player
        VideoPlayer(
            context = context,
            post = post,
            isPlaying = isPlaying,
            isMuted = isMuted,
            modifier = Modifier.fillMaxSize()
        )
        
        // Controls overlay
        if (showVideoControls) {
            VideoControls(
                isPlaying = isPlaying,
                isMuted = isMuted,
                onPlayPause = { isPlaying = !isPlaying },
                onMuteToggle = { isMuted = !isMuted },
                onClose = onClose,
                post = post
            )
        }
    }
}

@Composable
private fun VideoPlayer(
    context: Context,
    post: RedditPost,
    isPlaying: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUrl = getVideoUrl(post)
            if (videoUrl != null) {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = isPlaying
                volume = if (isMuted) 0f else 1f
            }
        }
    }
    
    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }
    
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }
    
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
            }
        },
        modifier = modifier
    )
}

@Composable
private fun VideoFeedOverlay(
    subreddit: String,
    currentIndex: Int,
    totalVideos: Int,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.1f)
            )
    ) {
        // Top bar with subreddit info and video counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            // Video counter
            Text(
                text = "$currentIndex / $totalVideos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom area for video info (will be filled by individual video controls)
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun VideoControls(
    isPlaying: Boolean,
    isMuted: Boolean,
    onPlayPause: () -> Unit,
    onMuteToggle: () -> Unit,
    onClose: () -> Unit,
    post: RedditPost
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.3f)
            )
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Center play/pause button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(40.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = post.title ?: "No title",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "u/${post.author ?: "unknown"} • r/${post.subreddit ?: "unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                // Mute button
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


// Helper functions
private fun isVideoPost(post: RedditPost): Boolean {
    return post.isVideo == true || 
           post.media?.redditVideo != null || 
           post.secureMedia?.redditVideo != null ||
           (post.url?.contains("youtube.com") == true) ||
           (post.url?.contains("youtu.be") == true) ||
           (post.url?.endsWith(".mp4") == true) ||
           (post.url?.endsWith(".webm") == true)
}

private fun getVideoUrl(post: RedditPost): String? {
    // Try Reddit video first
    post.media?.redditVideo?.fallbackUrl?.let { return it }
    post.secureMedia?.redditVideo?.fallbackUrl?.let { return it }
    
    // Try HLS URL
    post.media?.redditVideo?.hlsUrl?.let { return it }
    post.secureMedia?.redditVideo?.hlsUrl?.let { return it }
    
    // Try DASH URL
    post.media?.redditVideo?.dashUrl?.let { return it }
    post.secureMedia?.redditVideo?.dashUrl?.let { return it }
    
    // Try direct URL
    post.url?.let { url ->
        if (url.endsWith(".mp4") || url.endsWith(".webm")) {
            return url
        }
    }
    
    // Handle YouTube URLs
    post.url?.let { url ->
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            return convertYouTubeToEmbed(url)
        }
    }
    
    return null
}

private fun convertYouTubeToEmbed(url: String): String? {
    return try {
        val videoId = when {
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("v=").substringBefore("&")
            }
            else -> null
        }
        
        videoId?.let { "https://www.youtube.com/embed/$it?autoplay=1&mute=0&controls=1" }
    } catch (e: Exception) {
        null
    }
}

private fun formatTime(timestamp: Double?): String {
    if (timestamp == null) return "unknown"
    
    val currentTime = System.currentTimeMillis() / 1000.0
    val diff = currentTime - timestamp
    
    return when {
        diff < 60 -> "${diff.toInt()}s"
        diff < 3600 -> "${(diff / 60).toInt()}m"
        diff < 86400 -> "${(diff / 3600).toInt()}h"
        diff < 2592000 -> "${(diff / 86400).toInt()}d"
        else -> {
            val date = Date((timestamp * 1000).toLong())
            val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
            formatter.format(date)
        }
    }
}
