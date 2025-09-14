package com.rookie.code.substream.data.api

import com.rookie.code.substream.data.model.RedditReelResponse
import com.rookie.code.substream.data.model.SubredditListResponse
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

    /**
     * Get hot posts from a subreddit
     */
    suspend fun getSubredditPosts(subreddit: String, limit: Int = 25, after: String? = null): Resource<RedditReelResponse> {
        return safeCall {
            val endpoint = "r/$subreddit/hot.json"
            println("RedditApi: Calling endpoint: $endpoint with after: $after")
            client.get(endpoint) {
                url {
                    parameters.append("limit", limit.toString())
                    after?.let { parameters.append("after", it) }
                }
            }
        }
    }
}
