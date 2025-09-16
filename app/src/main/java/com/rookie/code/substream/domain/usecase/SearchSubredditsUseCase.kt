package com.rookie.code.substream.domain.usecase

import com.rookie.code.substream.data.utils.Resource
import com.rookie.code.substream.data.model.Subreddit
import com.rookie.code.substream.domain.repository.SubredditRepository
/**
 * Use case for searching subreddits
 * This encapsulates the business logic for searching subreddits
 */
class SearchSubredditsUseCase constructor(
    private val subredditRepository: SubredditRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 25, after: String?): Resource<Pair<List<Subreddit>, String?>> {
        return subredditRepository.searchSubreddits(query, limit, after)
    }
}
