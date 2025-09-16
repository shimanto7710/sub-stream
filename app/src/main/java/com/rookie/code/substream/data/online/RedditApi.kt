package com.rookie.code.substream.data.online

import com.rookie.code.substream.data.constants.DataConstants
import com.rookie.code.substream.data.model.RedditPostsResponse
import com.rookie.code.substream.data.model.SubredditListResponse
import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.data.utils.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class RedditApi(
    private val client: HttpClient
) {

    /**
     * Get popular subreddits
     */
    suspend fun getPopularSubreddits(limit: Int = DataConstants.Api.DEFAULT_LIMIT, after: String? = null): Resource<SubredditListResponse> {
        return safeCall {
            val response = client.get(DataConstants.Api.ENDPOINT_POPULAR_SUBREDDITS) {
                url {
                    parameters.append(DataConstants.Api.PARAM_LIMIT, limit.toString())
                    after?.let { parameters.append(DataConstants.Api.PARAM_AFTER, it) }
                }
            }
            println("${DataConstants.LogTags.REDDIT_API}: ${DataConstants.ApiMessages.RESPONSE_STATUS.replace("{status}", response.status.toString())}")
            println("${DataConstants.LogTags.REDDIT_API}: ${DataConstants.ApiMessages.RESPONSE_HEADERS.replace("{headers}", response.headers.toString())}")
            response
        }
    }

    /**
     * Search subreddits
     */
    suspend fun searchSubreddits(query: String, limit: Int = DataConstants.Api.DEFAULT_LIMIT, after: String? = null): Resource<SubredditListResponse> {
        return safeCall {
            client.get(DataConstants.Api.ENDPOINT_SEARCH_SUBREDDITS) {
                url {
                    parameters.append(DataConstants.Api.PARAM_QUERY, query)
                    parameters.append(DataConstants.Api.PARAM_LIMIT, limit.toString())
                    parameters.append(DataConstants.Api.PARAM_TYPE, DataConstants.Api.PARAM_TYPE_SUBREDDIT)
                    after?.let { parameters.append(DataConstants.Api.PARAM_AFTER, it) }
                }
            }
        }
    }

    /**
     * Get posts from a subreddit with specified sorting
     */
    suspend fun getSubredditPosts(subreddit: String, sorting: String = DataConstants.Api.DEFAULT_SORTING, limit: Int = DataConstants.Api.DEFAULT_LIMIT, after: String? = null): Resource<RedditPostsResponse> {
        return safeCall {
            val endpoint = DataConstants.Api.ENDPOINT_SUBREDDIT_POSTS
                .replace("{subreddit}", subreddit)
                .replace("{sorting}", sorting)
//            println("${DataConstants.LogTags.REDDIT_API}: ${DataConstants.ApiMessages.CALLING_ENDPOINT.replace("{endpoint}", endpoint).replace("{after}", after ?: "null")}")
            client.get(endpoint) {
                url {
                    parameters.append(DataConstants.Api.PARAM_LIMIT, limit.toString())
                    after?.let { parameters.append(DataConstants.Api.PARAM_AFTER, it) }
                }
            }
        }
    }
}
