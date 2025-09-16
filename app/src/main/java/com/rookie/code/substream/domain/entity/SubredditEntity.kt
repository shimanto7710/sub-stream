package com.rookie.code.substream.domain.entity

/**
 * Domain entity for Subreddit
 * This represents the core business entity without any framework dependencies
 */
data class SubredditEntity(
    val id: String,
    val displayName: String,
    val displayNamePrefixed: String?,
    val title: String?,
    val description: String?,
    val publicDescription: String?,
    val subscribers: Int?,
    val activeUserCount: Int?,
    val iconUrl: String?,
    val bannerUrl: String?,
    val isNsfw: Boolean?,
    val isSubscribed: Boolean?,
    val createdUtc: Double?
)
