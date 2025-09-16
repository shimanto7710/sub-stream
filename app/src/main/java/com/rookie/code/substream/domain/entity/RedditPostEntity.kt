package com.rookie.code.substream.domain.entity

/**
 * Domain entity for Reddit Post
 * This represents the core business entity without any framework dependencies
 */
data class RedditPostEntity(
    val id: String,
    val title: String?,
    val author: String?,
    val subreddit: String?,
    val selftext: String?,
    val url: String?,
    val thumbnail: String?,
    val isVideo: Boolean?,
    val isNsfw: Boolean?,
    val isSpoiler: Boolean?,
    val ups: Int?,
    val downs: Int?,
    val numComments: Int?,
    val createdUtc: Double?,
    val media: MediaEntity?,
    val secureMedia: MediaEntity?
)

data class MediaEntity(
    val redditVideo: RedditVideoEntity?
)

data class RedditVideoEntity(
    val fallbackUrl: String?,
    val hlsUrl: String?,
    val dashUrl: String?,
    val duration: Int?,
    val isGif: Boolean?
)
