package com.rookie.code.substream.data.api

/**
 * Manages Reddit API tokens using RedditAuthApi
 */
class TokenManager(
    private val redditAuthApi: RedditAuthApi
) {
    
    /**
     * Refreshes the access token using the refresh token
     */
    suspend fun refreshAccessToken(): Result<TokenResponse> {
        return try {
            val refreshToken = SessionManager.getRefreshToken() 
                ?: return Result.failure(Exception("No refresh token available"))
            
            val response = redditAuthApi.refreshToken(refreshToken = refreshToken)
            
            // Update session with new tokens
            SessionManager.updateSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken ?: refreshToken,
                expiresIn = response.expiresIn
            )
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets a valid access token, refreshing if necessary
     */
    suspend fun getValidAccessToken(): Result<String> {
        return if (SessionManager.isTokenValid()) {
            val token = SessionManager.getAccessToken()
            if (token != null) {
                Result.success(token)
            } else {
                Result.failure(Exception("No access token available"))
            }
        } else {
            refreshAccessToken().map { it.accessToken }
        }
    }
}
