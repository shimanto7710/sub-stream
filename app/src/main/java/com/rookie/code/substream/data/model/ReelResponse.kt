package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RedditReelResponse(
    val kind: String? = null,
    val data: ListingData? = null
)

@Serializable
data class ListingData(
    val after: String? = null,
    val dist: Int? = null,
    val modhash: String? = null,
    @SerialName("geo_filter")
    val geoFilter: String? = null,
    val children: List<Child>? = null,
    val before: String? = null
)

@Serializable
data class Child(
    val kind: String? = null,
    val data: RedditPost? = null
)

@Serializable
data class RedditPost(
    val id: String? = null,
    val name: String? = null,
    val title: String? = null,
    val selftext: String? = null,
    val subreddit: String? = null,
    @SerialName("subreddit_name_prefixed")
    val subredditNamePrefixed: String? = null,
    val author: String? = null,
    @SerialName("author_fullname")
    val authorFullname: String? = null,
    val domain: String? = null,
    val url: String? = null,
    @SerialName("url_overridden_by_dest")
    val urlOverriddenByDest: String? = null,
    val permalink: String? = null,
    val created: Double? = null,
    @SerialName("created_utc")
    val createdUtc: Double? = null,
    val ups: Int? = null,
    val downs: Int? = null,
    val score: Int? = null,
    @SerialName("num_comments")
    val numComments: Int? = null,
    @SerialName("over_18")
    val over18: Boolean? = null,
    val archived: Boolean? = null,
    val thumbnail: String? = null,
    @SerialName("thumbnail_height")
    val thumbnailHeight: Int? = null,
    @SerialName("thumbnail_width")
    val thumbnailWidth: Int? = null,
    @SerialName("is_video")
    val isVideo: Boolean? = null,
    val media: MediaWrapper? = null,
    @SerialName("secure_media")
    val secureMedia: MediaWrapper? = null,
    @SerialName("secure_media_embed")
    val secureMediaEmbed: Map<String, String>? = null,
    @SerialName("media_embed")
    val mediaEmbed: Map<String, String>? = null,
    val preview: Preview? = null,
    @SerialName("is_self")
    val isSelf: Boolean? = null,
    val stickied: Boolean? = null,
    val locked: Boolean? = null,
    @SerialName("num_crossposts")
    val numCrossposts: Int? = null,
    @SerialName("is_reddit_media_domain")
    val isRedditMediaDomain: Boolean? = null,
    @SerialName("post_hint")
    val postHint: String? = null,
    @SerialName("link_flair_text")
    val linkFlairText: String? = null,
    @SerialName("link_flair_background_color")
    val linkFlairBackgroundColor: String? = null,
    @SerialName("link_flair_css_class")
    val linkFlairCssClass: String? = null,
    @SerialName("author_premium")
    val authorPremium: Boolean? = null,
    val saved: Boolean? = null,
    val visited: Boolean? = null,
    @SerialName("all_awardings")
    val allAwardings: List<String>? = null,
    val awarders: List<String>? = null
)

@Serializable
data class MediaWrapper(
    @SerialName("reddit_video")
    val redditVideo: RedditVideo? = null
)

@Serializable
data class RedditVideo(
    @SerialName("bitrate_kbps")
    val bitrateKbps: Int? = null,
    @SerialName("fallback_url")
    val fallbackUrl: String? = null,
    val height: Int? = null,
    val width: Int? = null,
    @SerialName("scrubber_media_url")
    val scrubberMediaUrl: String? = null,
    @SerialName("dash_url")
    val dashUrl: String? = null,
    @SerialName("hls_url")
    val hlsUrl: String? = null,
    val duration: Int? = null,
    @SerialName("is_gif")
    val isGif: Boolean? = null,
    @SerialName("has_audio")
    val hasAudio: Boolean? = null,
    @SerialName("transcoding_status")
    val transcodingStatus: String? = null
)

@Serializable
data class Preview(
    val images: List<PreviewImage>? = null,
    val enabled: Boolean? = null
)

@Serializable
data class PreviewImage(
    val source: ImageSource? = null,
    val resolutions: List<ImageSource>? = null,
//    val variants: Map<String, String>? = null,
    val id: String? = null
)

@Serializable
data class ImageSource(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)
