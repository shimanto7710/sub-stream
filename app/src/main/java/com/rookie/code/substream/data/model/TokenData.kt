package com.rookie.code.substream.data.model

data class TokenData(
    val accessToken: String,
    val expiresAt: Long, // Timestamp when token expires
    val tokenType: String = "bearer"
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt
    }
    
    fun isExpiringSoon(bufferMinutes: Int = 5): Boolean {
        val bufferTime = bufferMinutes * 60 * 1000L
        return System.currentTimeMillis() >= (expiresAt - bufferTime)
    }
}
