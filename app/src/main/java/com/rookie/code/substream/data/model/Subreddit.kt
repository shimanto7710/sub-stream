package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subreddit(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("display_name_prefixed")
    val displayNamePrefixed: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("subscribers")
    val subscribers: Int,
    @SerialName("active_user_count")
    val activeUserCount: Int? = null,
    @SerialName("icon_img")
    val iconUrl: String? = null,
    @SerialName("banner_img")
    val bannerUrl: String? = null,
    @SerialName("over18")
    val isOver18: Boolean = false,
    @SerialName("public_description")
    val publicDescription: String? = null
)

@Serializable
data class SubredditListResponse(
    @SerialName("data")
    val data: SubredditListData
)

@Serializable
data class SubredditListData(
    @SerialName("children")
    val children: List<SubredditChild>
)

@Serializable
data class SubredditChild(
    @SerialName("data")
    val data: Subreddit
)
