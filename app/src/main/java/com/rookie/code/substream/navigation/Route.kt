package com.rookie.code.substream.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    object Home : Route

    @Serializable
    data class Posts(
        val subreddit: String,
        val sorting: String = "hot"
    ) : Route

    @Serializable
    object Videos : Route
}
