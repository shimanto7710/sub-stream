package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
/*

@Serializable
data class RedditPost(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("subreddit") val subreddit: String,
    @SerialName("url") val url: String,
    @SerialName("permalink") val permalink: String,
    @SerialName("created_utc") val createdUtc: Double,
    @SerialName("score") val score: Int,
    @SerialName("num_comments") val numComments: Int,
    @SerialName("is_video") val isVideo: Boolean = false,
    @SerialName("media") val media: RedditMedia? = null,
    @SerialName("preview") val preview: RedditPreview? = null,
    @SerialName("selftext") val selfText: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("over_18") val over18: Boolean = false
)

@Serializable
data class RedditMedia(
    @SerialName("reddit_video") val redditVideo: RedditVideo? = null
)

@Serializable
data class RedditVideo(
    @SerialName("fallback_url") val fallbackUrl: String? = null,
    @SerialName("hls_url") val hlsUrl: String? = null,
    @SerialName("is_gif") val isGif: Boolean = false,
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
    @SerialName("children") val children: List<RedditPostWrapper>
)

@Serializable
data class RedditPostWrapper(
    @SerialName("data") val data: RedditPost
)

@Serializable
data class RedditPostsResponse(
    @SerialName("data") val data: RedditPostData
)
*/
