package com.rookie.code.substream.presentation.screen

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.rookie.code.substream.data.model.ReelPost
import com.rookie.code.substream.presentation.viewmodel.VideoViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    println("VideoScreen: Composing VideoScreen")

    LaunchedEffect(Unit) {
        println("VideoScreen: Loading posts from r/videos")
        viewModel.loadPosts("videos") // Load from r/videos subreddit
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isFullscreen) {
            FullscreenVideoPlayer(
                post = uiState.currentVideoPost,
                onExitFullscreen = { viewModel.exitFullscreen() },
                onVideoEnd = { viewModel.playNextVideo() }
            )
        } else {
            VideoList(
                posts = uiState.posts,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onVideoClick = { post -> viewModel.playVideo(post) },
                onRetry = { viewModel.loadPosts("videos") }
            )
        }
    }
}

@Composable
private fun VideoList(
    posts: List<ReelPost>,
    isLoading: Boolean,
    error: String?,
    onVideoClick: (ReelPost) -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (error != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        items(posts) { post ->
            VideoPostCard(
                post = post,
                onClick = { onVideoClick(post) }
            )
        }
    }
}

@Composable
private fun VideoPostCard(
    post: ReelPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Video thumbnail or preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (post.isVideo == true || post.media?.type == "youtube.com" || post.secureMedia?.type == "youtube.com") {
                    // Show video thumbnail
                    Text(
                        text = "▶️ Video",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else if (post.preview?.images?.isNotEmpty() == true) {
                    // Show image preview
                    Text(
                        text = "🖼️ Image",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Text(
                        text = "📄 Post",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // Post details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = post.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "r/${post.subreddit ?: "unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "u/${post.author ?: "unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Score",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.score ?: 0}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Comments",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.numComments ?: 0}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FullscreenVideoPlayer(
    post: ReelPost?,
    onExitFullscreen: () -> Unit,
    onVideoEnd: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    LaunchedEffect(post) {
        if (post != null && (post.isVideo == true || post.media?.type == "youtube.com" || post.secureMedia?.type == "youtube.com" || post.domain == "youtube.com")) {
            exoPlayer?.release()
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                // Get video URL from the post data
                val videoUrl = when {
                    // First try the direct URL field
                    !post.url.isNullOrEmpty() -> post.url
                    // Then try url_overridden_by_dest (if it exists in our model)
                    !post.urlOverriddenByDest.isNullOrEmpty() -> post.urlOverriddenByDest
                    // Fallback to extracting from HTML iframe
                    post.media?.oembed?.html != null -> {
                        val regex = """src="([^"]+)"""".toRegex()
                        regex.find(post.media!!.oembed!!.html!!)?.groupValues?.get(1)
                    }
                    post.secureMedia?.oembed?.html != null -> {
                        val regex = """src="([^"]+)"""".toRegex()
                        regex.find(post.secureMedia!!.oembed!!.html!!)?.groupValues?.get(1)
                    }
                    else -> null
                }
                
                println("VideoScreen: Video URL found: $videoUrl")
                
                if (videoUrl != null) {
                    // Check if it's a YouTube URL
                    if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                        println("VideoScreen: YouTube URL detected, will use WebView")
                        // For YouTube URLs, we'll use WebView instead of ExoPlayer
                        // The WebView will be handled in the UI
                    } else {
                        // For direct video URLs, use ExoPlayer
                        val mediaItem = MediaItem.fromUri(videoUrl)
                        setMediaItem(mediaItem)
                        playWhenReady = true
                        volume = if (isMuted) 0f else 1f
                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                if (playbackState == Player.STATE_ENDED) {
                                    onVideoEnd()
                                }
                            }
                        })
                        prepare()
                    }
                } else {
                    println("VideoScreen: No video URL found for post: ${post.title}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }

    BackHandler {
        onExitFullscreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Determine if we should use WebView for YouTube or ExoPlayer for direct videos
        val videoUrl = post?.url ?: post?.urlOverriddenByDest
        val isYouTubeVideo = videoUrl?.contains("youtube.com") == true || videoUrl?.contains("youtu.be") == true
        
        if (isYouTubeVideo && videoUrl != null) {
            // Use WebView for YouTube videos
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(false)
                            builtInZoomControls = false
                            displayZoomControls = false
                        }
                        
                        // Convert YouTube URL to embed format with autoplay
                        val embedUrl = if (videoUrl.contains("youtube.com/watch")) {
                            val videoId = videoUrl.substringAfter("v=").substringBefore("&")
                            "https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&controls=1&rel=0&modestbranding=1"
                        } else if (videoUrl.contains("youtu.be/")) {
                            val videoId = videoUrl.substringAfter("youtu.be/").substringBefore("?")
                            "https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&controls=1&rel=0&modestbranding=1"
                        } else {
                            videoUrl
                        }
                        
                        println("VideoScreen: Loading YouTube embed URL: $embedUrl")
                        
                        loadUrl(embedUrl)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Use ExoPlayer for direct video URLs
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPlaying = !isPlaying }
        ) {
            // Top controls
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onExitFullscreen,
                    modifier = Modifier.background(
                        Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White
                    )
                }
            }

            // Center play button (for both YouTube and ExoPlayer videos)
            IconButton(
                onClick = {
                    if (isYouTubeVideo) {
                        // For YouTube videos, we can't control playback directly
                        // The WebView will handle the play action
                        println("VideoScreen: YouTube play button clicked")
                    } else {
                        // For ExoPlayer videos
                        isPlaying = !isPlaying
                        exoPlayer?.playWhenReady = isPlaying
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = if (isYouTubeVideo) Icons.Default.PlayArrow else 
                                 if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isYouTubeVideo) "Play YouTube Video" else 
                                       if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Bottom controls (only for ExoPlayer videos)
            if (!isYouTubeVideo) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            exoPlayer?.volume = if (isMuted) 0f else 1f
                        },
                        modifier = Modifier.background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
