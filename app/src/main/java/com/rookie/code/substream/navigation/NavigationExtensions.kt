package com.rookie.code.substream.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateToRoute(
    route: Route,
    builder: NavOptionsBuilder.() -> Unit = {
        // Default navigation options
        launchSingleTop = true
        restoreState = true
    }
) {
    navigate(route, builder)
}

fun NavController.navigateToPosts(subreddit: String) {
    navigateToRoute(Route.Posts(subreddit))
}

fun NavController.navigateToVideos() {
    navigateToRoute(Route.Videos)
}

fun NavController.navigateToHome() {
    navigateToRoute(Route.Home)
}

fun NavController.navigateBack() {
    if (!popBackStack()) {
        // If we can't pop back, we're at the root
        // Handle this case as needed
    }
}
