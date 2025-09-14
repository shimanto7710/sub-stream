package com.rookie.code.substream.data.repository

import com.rookie.code.substream.data.api.RedditApi
import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.api.map
import com.rookie.code.substream.data.model.ReelPost
import com.rookie.code.substream.domain.repository.RedditPostsRepository

class RedditPostsRepositoryImpl(
    private val redditApi: RedditApi
) : RedditPostsRepository {

    override suspend fun getSubredditPosts(subreddit: String, limit: Int): Resource<List<ReelPost>> {
        return redditApi.getSubredditPosts(subreddit, limit).map { response ->
            response.data?.children?.mapNotNull { it?.data } ?: emptyList()
        }
    }
}
