package com.rookie.code.substream.presentation.composables

import android.content.Context
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.entity.PostSortingEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Video feed screen that displays a vertical pager of videos
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeedScreen(
    posts: List<RedditPostEntity>,
    subreddit: String,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentVideoIndex by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) } // Global mute state for all videos
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { posts.size }
    )

    // Sync pager state with current video index
    LaunchedEffect(pagerState.currentPage) {
        currentVideoIndex = pagerState.currentPage

        // Load more videos when user reaches near the end (last 3 videos)
        if (currentVideoIndex >= posts.size - 3) {
            onLoadMore()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Vertical scrolling videos using VerticalPager
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            FullScreenVideoPlayer(
                post = posts[page],
                onClose = onBack,
                showControls = false,
                isCurrentVideo = page == currentVideoIndex,
                isMuted = isMuted,
                onMuteToggle = { isMuted = !isMuted },
                modifier = Modifier.fillMaxSize()
            )
        }

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

/**
 * Full screen video player with controls
 */
@Composable
fun FullScreenVideoPlayer(
    post: RedditPostEntity,
    onClose: () -> Unit,
    showControls: Boolean = false,
    isCurrentVideo: Boolean = true,
    isMuted: Boolean = false,
    onMuteToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(isCurrentVideo) }
    var showVideoControls by remember { mutableStateOf(showControls) }

    // Auto-hide controls after 3 seconds
    LaunchedEffect(showVideoControls) {
        if (showVideoControls) {
            kotlinx.coroutines.delay(3000)
            showVideoControls = false
        }
    }

    // Pause video when not current and hide controls when video changes
    LaunchedEffect(isCurrentVideo) {
        isPlaying = isCurrentVideo
        if (isCurrentVideo) {
            showVideoControls = false // Hide controls when switching to a new video
        }
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

        // Back button (always visible)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Mute/Unmute button (always visible)
        Button(
            onClick = {
                onMuteToggle()
                println("FullScreenVideoPlayer: Mute toggled to $isMuted")
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = if (isMuted) "🔇 MUTE" else "🔊 UNMUTE",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Controls overlay
        if (showVideoControls) {
            VideoControls(
                isPlaying = isPlaying,
                isMuted = isMuted,
                onPlayPause = {
                    isPlaying = !isPlaying
                    println("FullScreenVideoPlayer: Play/Pause toggled to $isPlaying")
                },
                onMuteToggle = onMuteToggle,
                onClose = onClose,
                post = post
            )
        }
    }
}

/**
 * Video player component using ExoPlayer
 */
@Composable
fun VideoPlayer(
    context: Context,
    post: RedditPostEntity,
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
                println("VideoPlayer: Initial volume set to ${if (isMuted) 0f else 1f}")
            }
        }
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
        println("VideoPlayer: Volume changed to ${if (isMuted) 0f else 1f}")
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

/**
 * Video controls overlay with play/pause and mute buttons
 */
@Composable
fun VideoControls(
    isPlaying: Boolean,
    isMuted: Boolean,
    onPlayPause: () -> Unit,
    onMuteToggle: () -> Unit,
    onClose: () -> Unit,
    post: RedditPostEntity
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
                if (isPlaying) {
                    // Custom pause icon (two vertical rectangles)
                    Canvas(
                        modifier = Modifier.size(40.dp)
                    ) {
                        val rectWidth = size.width * 0.25f
                        val rectHeight = size.height * 0.6f
                        val spacing = size.width * 0.1f
                        val startY = (size.height - rectHeight) / 2

                        // Left rectangle
                        drawRect(
                            color = Color.White,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                (size.width - rectWidth * 2 - spacing) / 2,
                                startY
                            ),
                            size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
                        )

                        // Right rectangle
                        drawRect(
                            color = Color.White,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                (size.width - rectWidth * 2 - spacing) / 2 + rectWidth + spacing,
                                startY
                            ),
                            size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
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
                    if (isMuted) {
                        // Muted icon (speaker with X)
                        Canvas(
                            modifier = Modifier.size(24.dp)
                        ) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val speakerWidth = size.width * 0.4f
                            val speakerHeight = size.height * 0.6f

                            // Speaker cone
                            drawRect(
                                color = Color.White,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - speakerWidth / 2,
                                    centerY - speakerHeight / 2
                                ),
                                size = androidx.compose.ui.geometry.Size(speakerWidth, speakerHeight)
                            )

                            // Speaker handle
                            drawRect(
                                color = Color.White,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - speakerWidth / 2 - size.width * 0.1f,
                                    centerY - speakerHeight * 0.3f
                                ),
                                size = androidx.compose.ui.geometry.Size(size.width * 0.1f, speakerHeight * 0.6f)
                            )

                            // X mark
                            val strokeWidth = 2.dp.toPx()
                            drawLine(
                                color = Color.Red,
                                start = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + size.width * 0.1f,
                                    centerY - speakerHeight / 2
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + size.width * 0.3f,
                                    centerY + speakerHeight / 2
                                ),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color.Red,
                                start = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + size.width * 0.3f,
                                    centerY - speakerHeight / 2
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + size.width * 0.1f,
                                    centerY + speakerHeight / 2
                                ),
                                strokeWidth = strokeWidth
                            )
                        }
                    } else {
                        // Unmuted icon (speaker)
                        Canvas(
                            modifier = Modifier.size(24.dp)
                        ) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val speakerWidth = size.width * 0.4f
                            val speakerHeight = size.height * 0.6f

                            // Speaker cone
                            drawRect(
                                color = Color.White,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - speakerWidth / 2,
                                    centerY - speakerHeight / 2
                                ),
                                size = androidx.compose.ui.geometry.Size(speakerWidth, speakerHeight)
                            )

                            // Speaker handle
                            drawRect(
                                color = Color.White,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    centerX - speakerWidth / 2 - size.width * 0.1f,
                                    centerY - speakerHeight * 0.3f
                                ),
                                size = androidx.compose.ui.geometry.Size(size.width * 0.1f, speakerHeight * 0.6f)
                            )

                            // Sound waves
                            val waveSpacing = size.width * 0.15f
                            val waveHeight = size.height * 0.2f

                            // First wave
                            drawLine(
                                color = Color.White,
                                start = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + waveSpacing,
                                    centerY
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + waveSpacing,
                                    centerY - waveHeight
                                ),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Second wave
                            drawLine(
                                color = Color.White,
                                start = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + waveSpacing * 2,
                                    centerY
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    centerX + speakerWidth / 2 + waveSpacing * 2,
                                    centerY - waveHeight * 1.5f
                                ),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Content shown when no videos are found
 */
@Composable
fun NoVideosContent(
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onRetry) {
                Text("Refresh")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back to Search")
            }
        }
    }
}

// Helper functions

/**
 * Checks if a post contains video content
 */
fun isVideoPost(post: RedditPostEntity): Boolean {
    return post.isVideo == true ||
            post.media?.redditVideo != null ||
            post.secureMedia?.redditVideo != null ||
            (post.url?.endsWith(".mp4") == true) ||
            (post.url?.endsWith(".webm") == true)
    // Note: YouTube videos are excluded because ExoPlayer can't play them directly
}

/**
 * Extracts video URL from a Reddit post
 */
fun getVideoUrl(post: RedditPostEntity): String? {
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

    // Handle YouTube URLs - ExoPlayer can't play YouTube directly
    post.url?.let { url ->
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            return null // Don't try to play YouTube videos with ExoPlayer
        }
    }

    return null
}
