package com.rookie.code.substream.data.repository

import com.rookie.code.substream.data.api.RedditApi
import com.rookie.code.substream.data.api.Resource
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.domain.repository.SubredditRepository
import com.rookie.code.substream.data.api.map

class SubredditRepositoryImpl(
    private val redditApi: RedditApi
) : SubredditRepository {

    override suspend fun getPopularSubreddits(limit: Int): Resource<List<Subreddit>> {
        return redditApi.getPopularSubreddits(limit).map { response -> 
            response.data.children.map { it.data }
        }
    }

    override suspend fun searchSubreddits(query: String, limit: Int): Resource<List<Subreddit>> {
        return redditApi.searchSubreddits(query, limit).map { response -> 
            response.data.children.map { it.data }
        }
    }
}
