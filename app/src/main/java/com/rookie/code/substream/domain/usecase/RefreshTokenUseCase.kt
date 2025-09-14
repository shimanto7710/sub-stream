package com.rookie.code.substream.domain.usecase

import com.rookie.code.substream.domain.repository.RedditRepository

class RefreshTokenUseCase(
    private val redditRepository: RedditRepository
) {
    suspend operator fun invoke(): Result<String> {
        return redditRepository.refreshAccessToken()
    }
}
