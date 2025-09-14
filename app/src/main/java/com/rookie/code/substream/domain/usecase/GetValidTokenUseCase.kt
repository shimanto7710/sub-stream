package com.rookie.code.substream.domain.usecase

import com.rookie.code.substream.domain.repository.RedditRepository

class GetValidTokenUseCase(
    private val redditRepository: RedditRepository
) {
    suspend operator fun invoke(): String? {
        return redditRepository.getValidAccessToken()
    }
}
