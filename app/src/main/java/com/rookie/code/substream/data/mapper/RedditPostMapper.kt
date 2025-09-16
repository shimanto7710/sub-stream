package com.rookie.code.substream.data.mapper

import com.rookie.code.substream.data.model.RedditPost
import com.rookie.code.substream.domain.entity.MediaEntity
import com.rookie.code.substream.domain.entity.RedditPostEntity
import com.rookie.code.substream.domain.entity.RedditVideoEntity

object RedditPostMapper {
    fun toDomainEntity(dataModel: RedditPost): RedditPostEntity {
        return RedditPostEntity(
            id = dataModel.id ?: "",
            title = dataModel.title,
            author = dataModel.author,
            subreddit = dataModel.subreddit,
            selftext = dataModel.selftext,
            url = dataModel.url,
            thumbnail = dataModel.thumbnail,
            isVideo = dataModel.isVideo,
            isNsfw = dataModel.isNsfw,
            isSpoiler = dataModel.isSpoiler,
            ups = dataModel.ups,
            downs = dataModel.downs,
            numComments = dataModel.numComments,
            createdUtc = dataModel.createdUtc,
            media = dataModel.media?.let { media ->
                MediaEntity(
                    redditVideo = media.redditVideo?.let { video ->
                        RedditVideoEntity(
                            fallbackUrl = video.fallbackUrl,
                            hlsUrl = video.hlsUrl,
                            dashUrl = video.dashUrl,
                            duration = video.duration,
                            isGif = video.isGif
                        )
                    }
                )
            },
            secureMedia = dataModel.secureMedia?.let { media ->
                MediaEntity(
                    redditVideo = media.redditVideo?.let { video ->
                        RedditVideoEntity(
                            fallbackUrl = video.fallbackUrl,
                            hlsUrl = video.hlsUrl,
                            dashUrl = video.dashUrl,
                            duration = video.duration,
                            isGif = video.isGif
                        )
                    }
                )
            }
        )
    }
    
    fun toDomainEntities(dataModels: List<RedditPost>): List<RedditPostEntity> {
        return dataModels.map { toDomainEntity(it) }
    }
}
