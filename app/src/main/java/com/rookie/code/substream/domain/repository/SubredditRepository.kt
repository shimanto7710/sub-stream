package com.rookie.code.substream.domain.repository

import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.model.Subreddit

interface SubredditRepository {
    suspend fun getPopularSubreddits(limit: Int = 25): Resource<List<Subreddit>>
    suspend fun searchSubreddits(query: String, limit: Int = 25): Resource<List<Subreddit>>
}
