package com.rookie.code.substream.domain.repository

interface RedditRepository {
    suspend fun refreshAccessToken(): Result<String>
    suspend fun getValidAccessToken(): String?
    fun setRefreshToken(refreshToken: String)
    fun clearTokens()
}
