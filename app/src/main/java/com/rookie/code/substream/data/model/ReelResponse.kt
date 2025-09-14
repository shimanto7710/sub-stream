package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReelResponse(
    @SerialName("data")
    val data: ReelData?,
    @SerialName("kind")
    val kind: String?
)

@Serializable
data class ReelData(
    @SerialName("after")
    val after: String?,
    @SerialName("before")
    val before: String?,
    @SerialName("children")
    val children: List<ReelPostWrapper?>?,
    @SerialName("dist")
    val dist: Int?,
    @SerialName("geo_filter")
    val geoFilter: String?,
    @SerialName("modhash")
    val modhash: String?
)

@Serializable
data class ReelPostWrapper(
    @SerialName("data")
    val data: ReelPost?,
    @SerialName("kind")
    val kind: String?
)

@Serializable
data class ReelPost(
    @SerialName("id")
    val id: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("author")
    val author: String?,
    @SerialName("subreddit")
    val subreddit: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("url_overridden_by_dest")
    val urlOverriddenByDest: String?,
    @SerialName("domain")
    val domain: String?,
    @SerialName("permalink")
    val permalink: String?,
    @SerialName("created_utc")
    val createdUtc: Double?,
    @SerialName("score")
    val score: Int?,
    @SerialName("num_comments")
    val numComments: Int?,
    @SerialName("is_video")
    val isVideo: Boolean?,
    @SerialName("media")
    val media: ReelMedia?,
    @SerialName("secure_media")
    val secureMedia: ReelSecureMedia?,
    @SerialName("preview")
    val preview: ReelPreview?,
    @SerialName("selftext")
    val selftext: String?,
    @SerialName("thumbnail")
    val thumbnail: String?,
    @SerialName("over_18")
    val over18: Boolean?,
    @SerialName("post_hint")
    val postHint: String?,
    @SerialName("subreddit_name_prefixed")
    val subredditNamePrefixed: String?,
    @SerialName("subreddit_subscribers")
    val subredditSubscribers: Int?,
    @SerialName("upvote_ratio")
    val upvoteRatio: Double?,
    @SerialName("ups")
    val ups: Int?
)

@Serializable
data class ReelMedia(
    @SerialName("oembed")
    val oembed: ReelOembed?,
    @SerialName("type")
    val type: String?
)

@Serializable
data class ReelSecureMedia(
    @SerialName("oembed")
    val oembed: ReelOembed?,
    @SerialName("type")
    val type: String?
)

@Serializable
data class ReelOembed(
    @SerialName("author_name")
    val authorName: String?,
    @SerialName("author_url")
    val authorUrl: String?,
    @SerialName("height")
    val height: Int?,
    @SerialName("html")
    val html: String?,
    @SerialName("provider_name")
    val providerName: String?,
    @SerialName("provider_url")
    val providerUrl: String?,
    @SerialName("thumbnail_height")
    val thumbnailHeight: Int?,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerialName("thumbnail_width")
    val thumbnailWidth: Int?,
    @SerialName("title")
    val title: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("version")
    val version: String?,
    @SerialName("width")
    val width: Int?
)

@Serializable
data class ReelPreview(
    @SerialName("enabled")
    val enabled: Boolean?,
    @SerialName("images")
    val images: List<ReelImage?>?
)

@Serializable
data class ReelImage(
    @SerialName("id")
    val id: String?,
    @SerialName("resolutions")
    val resolutions: List<ReelResolution?>?,
    @SerialName("source")
    val source: ReelImageSource?,
    @SerialName("variants")
    val variants: ReelVariants?
)

@Serializable
data class ReelResolution(
    @SerialName("height")
    val height: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("width")
    val width: Int?
)

@Serializable
data class ReelImageSource(
    @SerialName("height")
    val height: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("width")
    val width: Int?
)

@Serializable
data class ReelVariants(
    @SerialName("placeholder")
    val placeholder: String? = null
)