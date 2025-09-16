package com.rookie.code.substream.domain.usecase

import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.domain.repository.SubredditRepository
/**
 * Use case for getting popular subreddits
 * This encapsulates the business logic for fetching popular subreddits
 */
class GetPopularSubredditsUseCase constructor(
    private val subredditRepository: SubredditRepository
) {

    suspend operator fun invoke(limit: Int = 25, after: String?): Resource<Pair<List<Subreddit>, String?>> {
        return subredditRepository.getPopularSubreddits(limit, after)
    }
}
