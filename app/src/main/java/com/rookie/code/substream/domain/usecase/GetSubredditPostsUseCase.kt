package com.rookie.code.substream.domain.usecase

import com.rookie.code.substream.domain.entity.PostSortingEntity
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.repository.RedditPostsRepository
/**
 * Use case for getting subreddit posts
 * This encapsulates the business logic for fetching posts from a subreddit
 */
class GetSubredditPostsUseCase constructor(
    private val redditPostsRepository: RedditPostsRepository
) {
    suspend operator fun invoke(subreddit: String, sorting: PostSortingEntity, limit: Int = 25, after: String?): Result<Pair<List<RedditPostEntity>, String?>> {
        return redditPostsRepository.getSubredditPosts(subreddit, sorting, limit, after)
    }
}
