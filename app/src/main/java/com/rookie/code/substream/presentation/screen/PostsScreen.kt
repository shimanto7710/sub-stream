package com.rookie.code.substream.presentation.screen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var selectedVideoPost by remember { mutableStateOf<RedditPost?>(null) }

    LaunchedEffect(subreddit) {
        viewModel.loadPosts(subreddit)
    }

    // Show full-screen video player if a video is selected
    if (selectedVideoPost != null) {
        FullScreenVideoPlayer(
            post = selectedVideoPost!!,
            onClose = { selectedVideoPost = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "r/$subreddit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            when (uiState) {
                is PostsViewModel.PostsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PostsViewModel.PostsUiState.Error -> {
                    ErrorContent(
                        message = (uiState as PostsViewModel.PostsUiState.Error).message,
                        onRetry = { viewModel.loadPosts(subreddit) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            is PostsViewModel.PostsUiState.Success -> {
                val allPosts = (uiState as PostsViewModel.PostsUiState.Success).posts
                val videoPosts = allPosts.filter { isVideoPost(it) }
                
                if (videoPosts.isEmpty()) {
                    NoVideosContent(
                        message = "No videos found in r/$subreddit",
                        onRetry = { viewModel.loadPosts(subreddit) },
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    PostsList(
                        posts = videoPosts,
                        onVideoClick = { post -> selectedVideoPost = post },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun PostsList(
    posts: List<RedditPost>,
    onVideoClick: (RedditPost) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Video count header
            Text(
                text = "📹 ${posts.size} Video${if (posts.size == 1) "" else "s"} Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(posts) { post ->
            PostCard(
                post = post,
                onVideoClick = onVideoClick
            )
        }
    }
}

@Composable
private fun PostCard(
    post: RedditPost,
    onVideoClick: (RedditPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onVideoClick(post) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Text(
                text = post.title ?: "No title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Author and time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "u/${post.author ?: "unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = formatTime(post.createdUtc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Video thumbnail (all posts are videos)
            VideoThumbnail(
                post = post,
                onClick = { onVideoClick(post) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Self text if available
            post.selftext?.let { selftext ->
                if (selftext.isNotEmpty()) {
                    Text(
                        text = selftext,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Score",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.score ?: post.ups ?: 0}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Comments
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.numComments ?: 0}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Domain
                Text(
                    text = post.domain ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    
    // Auto-hide controls after 3 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            kotlinx.coroutines.delay(3000)
            showControls = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
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
        if (showControls) {
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
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            
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
            
            Text(
                text = "u/${post.author ?: "unknown"} • r/${post.subreddit ?: "unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun VideoThumbnail(
    post: RedditPost,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Video thumbnail
        post.thumbnail?.let { thumbnail ->
            if (thumbnail != "self" && thumbnail != "default" && thumbnail.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Video Thumbnail",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
        
        // Play button overlay
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play Video",
            tint = Color.White,
            modifier = Modifier
                .size(60.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(30.dp)
                )
        )
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
