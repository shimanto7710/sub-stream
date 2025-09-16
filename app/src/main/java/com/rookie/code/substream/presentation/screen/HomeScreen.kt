package com.rookie.code.substream.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rookie.code.substream.R
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.presentation.viewmodel.SubredditViewModel
import com.rookie.code.substream.presentation.viewmodel.SubredditUiState
import org.koin.androidx.compose.koinViewModel

object HomeScreenConstants {

    // Logging
    const val LOG_TAG = "HomeScreen"

    // Debug Messages
    const val SORT_BUTTON_CLICKED = "Sort button clicked"
    const val DROPDOWN_DISMISSED = "Dropdown dismissed"
    const val SORTING_CHANGED = "Sorting changed to {sorting}"
    const val SHOWING_ERROR = "Showing error - {error}"
    const val SHOWING_NO_SEARCH_RESULTS = "Showing no search results for query: {query}"
    const val SHOWING_EMPTY_CONTENT = "Showing empty content (no search performed)"
    const val SHOWING_SUBREDDITS = "Showing {count} subreddits"

    // Error Messages
    const val ERROR_UNKNOWN = "Unknown error occurred"

    // UI Constants
    const val SEARCH_BAR_HEIGHT = 56
    const val CARD_ELEVATION = 4
    const val ICON_SIZE = 40
    const val ERROR_ICON_SIZE = 80
    const val LOAD_MORE_ICON_SIZE = 24
    const val SPACING_SMALL = 4
    const val SPACING_MEDIUM = 8
    const val SPACING_LARGE = 16
    const val PADDING_SMALL = 8
    const val PADDING_MEDIUM = 16
    const val PADDING_LARGE = 24
    const val PADDING_XLARGE = 32
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSubredditClick: (String, PostSortingEntity) -> Unit = { _, _ -> }
) {
    val viewModel: SubredditViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    HomeScreenView(
        modifier = modifier,
        onSubredditClick = onSubredditClick,
        onSearchQueryChange = { query -> viewModel.searchSubreddits(query) },
        onSortingChange = { sorting -> viewModel.updateSorting(sorting) },
        onRetry = { viewModel.refresh() },
        onDismissError = { viewModel.clearError() },
        onLoadMore = { viewModel.loadMoreSubreddits() },
        onRefresh = { viewModel.refresh() },
        onClearSearch = { viewModel.searchSubreddits("") },
        uiState = uiState
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenView(
    modifier: Modifier = Modifier,
    onSubredditClick: (String, PostSortingEntity) -> Unit = { _, _ -> },
    onSearchQueryChange: (String) -> Unit = {},
    onSortingChange: (PostSortingEntity) -> Unit = {},
    onRetry: () -> Unit = {},
    onDismissError: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onClearSearch: () -> Unit = {},
    uiState: SubredditUiState = SubredditUiState()
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    var showSortingMenu by remember { mutableStateOf(false) }
    val currentSorting = uiState.currentSorting

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SORT_BUTTON_CLICKED}")
                            showSortingMenu = true 
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = currentSorting.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    Icons.Default.MoreVert, 
                                    contentDescription = stringResource(R.string.cd_sort_icon),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        DropdownMenu(
                            expanded = showSortingMenu,
                            onDismissRequest = {
                                println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.DROPDOWN_DISMISSED}")
                                showSortingMenu = false
                            }
                        ) {
                            PostSortingEntity.values().forEach { sorting ->
                                val isSelected = sorting == currentSorting
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = sorting.displayName,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SORTING_CHANGED.replace("{sorting}", sorting.displayName)}")
                                        onSortingChange(sorting)
                                        showSortingMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            onSearchQueryChange(it)
                        },
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
                        uiState.error != null -> {
                            println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SHOWING_ERROR.replace("{error}", uiState.error ?: "")}")
                            ErrorContent(
                                error = uiState.error ?: HomeScreenConstants.ERROR_UNKNOWN,
                                onRetry = onRetry,
                                onDismiss = onDismissError
                            )
                        }
            
            uiState.subreddits.isEmpty() && uiState.hasSearched -> {
                println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SHOWING_NO_SEARCH_RESULTS.replace("{query}", uiState.searchQuery)}")
                NoSearchResultsContent(
                    searchQuery = uiState.searchQuery,
                    onClearSearch = {
                        searchQuery = ""
                        onClearSearch()
                    }
                )
            }
            
            uiState.subreddits.isEmpty() -> {
                println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SHOWING_EMPTY_CONTENT}")
                EmptyContent()
            }

            else -> {
                println("${HomeScreenConstants.LOG_TAG}: ${HomeScreenConstants.SHOWING_SUBREDDITS.replace("{count}", uiState.subreddits.size.toString())}")
                SubredditList(
                    subreddits = uiState.subreddits,
                    onRefresh = onRefresh,
                    onSubredditClick = { subreddit -> onSubredditClick(subreddit, currentSorting) },
                    isLoadingMore = uiState.isLoadingMore,
                    canLoadMore = uiState.canLoadMore,
                    onLoadMore = onLoadMore
                )
            }
        }
        }
    }
}

@Composable
private fun SubredditList(
    subreddits: List<Subreddit>,
    onRefresh: () -> Unit,
    onSubredditClick: (String) -> Unit,
    isLoadingMore: Boolean = false,
    canLoadMore: Boolean = true,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subreddits) { subreddit ->
            SubredditItem(
                subreddit = subreddit,
                onClick = { onSubredditClick(subreddit.displayName ?: "") }
            )
        }
        
        if (canLoadMore) {
            item {
                LoadMoreButton(
                    isLoading = isLoadingMore,
                    onLoadMore = onLoadMore
                )
            }
        }
    }
}

@Composable
private fun SubredditItem(
    subreddit: Subreddit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = subreddit.displayNamePrefixed ?: "r/${subreddit.displayName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (!subreddit.title.isNullOrBlank()) {
                Text(
                    text = subreddit.title ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!subreddit.publicDescription.isNullOrBlank()) {
                Text(
                    text = subreddit.publicDescription ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${subreddit.subscribers ?: 0} subscribers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                subreddit.activeUserCount?.let { activeUsers ->
                    Text(
                        text = "$activeUsers online",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_error_icon),
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                // Title
                Text(
                    text = stringResource(R.string.error_something_went_wrong),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                // Error message
                Text(
                    text = stringResource(R.string.error_loading_subreddits),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                // Technical error (if needed for debugging)
                if (error.isNotEmpty()) {
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.action_retry))
                    }
                    
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.action_dismiss))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No subreddits found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NoSearchResultsContent(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search icon with background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "No results",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Title
            Text(
                text = "No Results Found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            // Description
            Text(
                text = "We couldn't find any subreddits matching \"$searchQuery\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            // Suggestions
            Text(
                text = "Try searching with different keywords or check your spelling",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Clear search button
            Button(
                onClick = onClearSearch,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(stringResource(R.string.action_clear_search))
            }
        }
    }
}

@Composable
private fun LoadMoreButton(
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        } else {
            Button(
                onClick = onLoadMore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.load_more_button))
            }
        }
    }
}

// Preview composables
@Preview(showBackground = true)
@Composable
private fun HomeScreenViewPreview() {
    HomeScreenView(
        onSubredditClick = { _, _ -> },
        onSearchQueryChange = {},
        onSortingChange = {},
        onRetry = {},
        onDismissError = {},
        onLoadMore = {},
        onRefresh = {},
        onClearSearch = {},
        uiState = SubredditUiState(
            isLoading = false,
            subreddits = listOf(
                Subreddit(
                    id = "1",
                    displayName = "androiddev",
                    displayNamePrefixed = "r/androiddev",
                    title = "Android Development",
                    publicDescription = "A subreddit for Android developers to discuss Android development, programming, and related topics.",
                    subscribers = 150000,
                    activeUserCount = 2500,
                    isNsfw = false,
                    isSubscribed = false,
                    createdUtc = 1234567890.0
                ),
                Subreddit(
                    id = "2",
                    displayName = "kotlin",
                    displayNamePrefixed = "r/kotlin",
                    title = "Kotlin Programming Language",
                    publicDescription = "Discussion about Kotlin, statically typed programming language for the JVM, Android, browser, and native.",
                    subscribers = 45000,
                    activeUserCount = 800,
                    isNsfw = false,
                    isSubscribed = false,
                    createdUtc = 1234567890.0
                ),
                Subreddit(
                    id = "3",
                    displayName = "compose",
                    displayNamePrefixed = "r/compose",
                    title = "Jetpack Compose",
                    publicDescription = "A modern toolkit for building native Android UIs with Jetpack Compose.",
                    subscribers = 25000,
                    activeUserCount = 400,
                    isNsfw = false,
                    isSubscribed = false,
                    createdUtc = 1234567890.0
                )
            ),
            error = null,
            searchQuery = "",
            isSearching = false,
            hasSearched = false,
            isLoadingMore = false,
            canLoadMore = true,
            currentAfter = null
        )
    )
}
