package com.rookie.code.substream.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RedditError(
    val message: String,
    val error: String? = null,
    val status: Int? = null
)
