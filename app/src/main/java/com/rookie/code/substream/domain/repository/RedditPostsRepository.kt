package com.rookie.code.substream.domain.repository

import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.model.RedditPost

interface RedditPostsRepository {
    suspend fun getSubredditPosts(subreddit: String, limit: Int = 25): Resource<List<RedditPost>>
}