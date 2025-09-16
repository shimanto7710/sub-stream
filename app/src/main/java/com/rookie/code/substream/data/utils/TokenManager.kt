package com.rookie.code.substream.data.utils

import com.rookie.code.substream.data.constants.DataConstants
import com.rookie.code.substream.data.online.RedditAuthApi
import com.rookie.code.substream.data.online.TokenResponse

class TokenManager(
    private val redditAuthApi: RedditAuthApi
) {
    
    /**
     * Refreshes the access token using the refresh token
     */
    suspend fun refreshAccessToken(): Result<TokenResponse> {
        return try {
            val refreshToken = SessionManager.getRefreshToken()
                ?: return Result.failure(Exception(DataConstants.ErrorMessages.NO_REFRESH_TOKEN))
            
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
                    Result.failure(Exception(DataConstants.ErrorMessages.NO_ACCESS_TOKEN))
            }
        } else {
            refreshAccessToken().map { it.accessToken }
        }
    }
}
