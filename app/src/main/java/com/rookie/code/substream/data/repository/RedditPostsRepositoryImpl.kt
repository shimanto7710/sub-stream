package com.rookie.code.substream.data.repository

import com.rookie.code.substream.data.api.RedditApi
import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.api.map
import com.rookie.code.substream.data.model.RedditPost
import com.rookie.code.substream.data.model.PostSorting
import com.rookie.code.substream.domain.repository.RedditPostsRepository

class RedditPostsRepositoryImpl(
    private val redditApi: RedditApi
) : RedditPostsRepository {

    override suspend fun getSubredditPosts(subreddit: String, limit: Int): Resource<List<RedditPost>> {
        return redditApi.getSubredditPosts(subreddit, "hot", limit).map { response ->
            response.data?.children?.mapNotNull { child ->
                child?.data?.let { postData ->
                    postData
                }
            } ?: emptyList()
        }
    }

    override suspend fun getSubredditPosts(subreddit: String, limit: Int, after: String?): Resource<Pair<List<RedditPost>, String?>> {
        return redditApi.getSubredditPosts(subreddit, "hot", limit, after).map { response ->
            val posts = response.data?.children?.mapNotNull { child ->
                child?.data?.let { postData ->
                    postData
                }
            } ?: emptyList()
            val nextAfter = response.data?.after
            Pair(posts, nextAfter)
        }
    }

    override suspend fun getSubredditPosts(subreddit: String, sorting: PostSorting, limit: Int): Resource<List<RedditPost>> {
        return redditApi.getSubredditPosts(subreddit, sorting.apiValue, limit).map { response ->
            response.data?.children?.mapNotNull { child ->
                child?.data?.let { postData ->
                    postData
                }
            } ?: emptyList()
        }
    }

    override suspend fun getSubredditPosts(subreddit: String, sorting: PostSorting, limit: Int, after: String?): Resource<Pair<List<RedditPost>, String?>> {
        return redditApi.getSubredditPosts(subreddit, sorting.apiValue, limit, after).map { response ->
            val posts = response.data?.children?.mapNotNull { child ->
                child?.data?.let { postData ->
                    postData
                }
            } ?: emptyList()
            val nextAfter = response.data?.after
            Pair(posts, nextAfter)
        }
    }

}