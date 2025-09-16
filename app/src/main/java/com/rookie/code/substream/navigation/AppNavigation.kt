package com.rookie.code.substream.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rookie.code.substream.presentation.screen.HomeScreen
import com.rookie.code.substream.presentation.screen.PostsScreen
import com.rookie.code.substream.presentation.viewmodel.PostsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(220, delayMillis = 90)
            ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(220)) + fadeOut(
                animationSpec = tween(220)
            )
        },
    ) {
        composable<Route.Home> {
            HomeScreen(
                onSubredditClick = { subreddit, sorting ->
                    navController.navigateToPosts(subreddit, sorting.apiValue)
                }
            )
        }

        composable<Route.Posts> {
            val args = it.toRoute<Route.Posts>()
            val sorting = com.rookie.code.substream.domain.entity.PostSortingEntity.values()
                .find { sorting -> sorting.apiValue == args.sorting } 
                ?: com.rookie.code.substream.domain.entity.PostSortingEntity.HOT
            
            PostsScreen(
                subreddit = args.subreddit,
                sorting = sorting,
                onBack = {
                    navController.navigateBack()
                },
                viewModel = koinViewModel<PostsViewModel>()
            )
        }

        // Videos route commented out since VideoScreen is not available
        // composable<Route.Videos> {
        //     VideoScreen()
        // }
    }
}
