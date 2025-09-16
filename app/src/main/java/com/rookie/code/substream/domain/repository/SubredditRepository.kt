package com.rookie.code.substream.domain.repository

import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.data.model.Subreddit


interface SubredditRepository {
    suspend fun getPopularSubreddits(limit: Int = 25): Resource<List<Subreddit>>
    suspend fun getPopularSubreddits(limit: Int = 25, after: String?): Resource<Pair<List<Subreddit>, String?>>
    suspend fun searchSubreddits(query: String, limit: Int = 25): Resource<List<Subreddit>>
    suspend fun searchSubreddits(query: String, limit: Int = 25, after: String?): Resource<Pair<List<Subreddit>, String?>>
}