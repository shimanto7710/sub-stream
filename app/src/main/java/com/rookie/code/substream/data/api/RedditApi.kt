package com.rookie.code.substream.data.api

import com.rookie.code.substream.data.model.SubredditListResponse
import com.rookie.code.substream.data.ktor.KtorClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get

/**
 * Reddit API class following the example ProfileApi pattern
 * All Reddit API calls are centralized here
 */
class RedditApi(
    private val client: HttpClient
) {

    /**
     * Get popular subreddits
     */
    suspend fun getPopularSubreddits(limit: Int = 25): Resource<SubredditListResponse> {
        return safeCall {
            // Ensure we have a valid access token before making the API call
            ensureValidToken()
            
            val response = client.get("subreddits/popular") {
                url {
                    parameters.append("limit", limit.toString())
                }
            }
            println("RedditApi: Response status: ${response.status}")
            println("RedditApi: Response headers: ${response.headers}")
            response
        }
    }
    
    private suspend fun ensureValidToken() {
        // Check if we have a valid access token
        val accessToken = com.rookie.code.substream.data.api.SessionManager.getAccessToken()
        println("RedditApi: Current access token: ${accessToken?.take(10)}...")
        
        if (accessToken.isNullOrEmpty()) {
            println("RedditApi: No access token found, attempting to refresh...")
            try {
                // Force a token refresh
                val refreshResult = KtorClient.refreshToken(io.ktor.client.engine.okhttp.OkHttp.create())
                if (refreshResult.isSuccess) {
                    println("RedditApi: Token refresh successful")
                } else {
                    println("RedditApi: Token refresh failed: ${refreshResult.responseCode}")
                }
            } catch (e: Exception) {
                println("RedditApi: Token refresh exception: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("RedditApi: Access token found, should work with Bearer auth")
        }
    }

    /**
     * Search subreddits
     */
    suspend fun searchSubreddits(query: String, limit: Int = 25): Resource<SubredditListResponse> {
        return safeCall {
            client.get("subreddits/search") {
                url {
                    parameters.append("q", query)
                    parameters.append("limit", limit.toString())
                    parameters.append("type", "sr")
                }
            }
        }
    }
}
