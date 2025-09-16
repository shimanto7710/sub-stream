package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RedditPost(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("subreddit") val subreddit: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("permalink") val permalink: String? = null,
    @SerialName("created_utc") val createdUtc: Double? = null,
    @SerialName("ups") val ups: Int? = null,
    @SerialName("downs") val downs: Int? = null,
    @SerialName("num_comments") val numComments: Int? = null,
    @SerialName("is_video") val isVideo: Boolean? = false,
    @SerialName("is_self") val isSelf: Boolean? = false,
    @SerialName("is_spoiler") val isSpoiler: Boolean? = false,
    @SerialName("over_18") val isNsfw: Boolean? = false,
    @SerialName("media") val media: RedditMedia? = null,
    @SerialName("secure_media") val secureMedia: RedditMedia? = null,
    @SerialName("preview") val preview: RedditPreview? = null,
    @SerialName("selftext") val selftext: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null
)

@Serializable
data class RedditMedia(
    @SerialName("reddit_video") val redditVideo: RedditVideo? = null
)

@Serializable
data class RedditVideo(
    @SerialName("fallback_url") val fallbackUrl: String? = null,
    @SerialName("hls_url") val hlsUrl: String? = null,
    @SerialName("dash_url") val dashUrl: String? = null,
    @SerialName("is_gif") val isGif: Boolean? = false,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null
)

@Serializable
data class RedditPreview(
    @SerialName("images") val images: List<RedditImage>? = null
)

@Serializable
data class RedditImage(
    @SerialName("source") val source: RedditImageSource? = null,
    @SerialName("resolutions") val resolutions: List<RedditImageSource>? = null
)

@Serializable
data class RedditImageSource(
    @SerialName("url") val url: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int
)

@Serializable
data class RedditPostData(
    @SerialName("children") val children: List<RedditPostWrapper>? = null,
    @SerialName("after") val after: String? = null,
    @SerialName("before") val before: String? = null,
    @SerialName("dist") val dist: Int? = null
)

@Serializable
data class RedditPostWrapper(
    @SerialName("data") val data: RedditPost? = null
)

@Serializable
data class RedditPostsResponse(
    @SerialName("data") val data: RedditPostData? = null
)
