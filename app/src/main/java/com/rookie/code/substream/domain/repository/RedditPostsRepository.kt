package com.rookie.code.substream.domain.repository

import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.domain.entity.RedditPostEntity


interface RedditPostsRepository {
    suspend fun getSubredditPosts(subreddit: String, sorting: PostSortingEntity, limit: Int = 25, after: String?): Result<Pair<List<RedditPostEntity>, String?>>
}