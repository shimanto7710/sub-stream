package com.rookie.code.substream.data.repository

import com.rookie.code.substream.data.online.RedditApi
import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.data.mapper.RedditPostMapper
import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.repository.RedditPostsRepository

class RedditPostsRepositoryImpl(
    private val redditApi: RedditApi
) : RedditPostsRepository {

    override suspend fun getSubredditPosts(subreddit: String, sorting: PostSortingEntity, limit: Int, after: String?): Result<Pair<List<RedditPostEntity>, String?>> {
        return when (val result = redditApi.getSubredditPosts(subreddit, sorting.apiValue, limit, after)) {
            is Resource.Success -> {
                val posts = result.data.data?.children?.mapNotNull { child ->
                    child?.data?.let { postData ->
                        postData
                    }
                } ?: emptyList()
                val nextAfter = result.data.data?.after
                Result.success(Pair(RedditPostMapper.toDomainEntities(posts), nextAfter))
            }
            is Resource.Error -> {
                Result.failure(result.exception)
            }
            is Resource.Loading -> {
                Result.failure(Exception("Loading"))
            }
        }
    }
}