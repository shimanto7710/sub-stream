package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subreddit(
    @SerialName("id")
    val id: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("display_name_prefixed")
    val displayNamePrefixed: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("subscribers")
    val subscribers: Int? = null,
    @SerialName("active_user_count")
    val activeUserCount: Int? = null,
    @SerialName("icon_img")
    val iconUrl: String? = null,
    @SerialName("banner_img")
    val bannerUrl: String? = null,
    @SerialName("over18")
    val isNsfw: Boolean? = false,
    @SerialName("user_is_subscriber")
    val isSubscribed: Boolean? = null,
    @SerialName("created_utc")
    val createdUtc: Double? = null,
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
    val children: List<SubredditChild>,
    @SerialName("after")
    val after: String? = null,
    @SerialName("before")
    val before: String? = null,
    @SerialName("dist")
    val dist: Int? = null
)

@Serializable
data class SubredditChild(
    @SerialName("data")
    val data: Subreddit
)
