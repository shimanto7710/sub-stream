package com.rookie.code.substream.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Reddit Authentication API interface
 * Handles token refresh and authentication
 */
interface RedditAuthApi {
    suspend fun refreshToken(
        grantType: String = "refresh_token",
        refreshToken: String
    ): TokenResponse
}


/**
 * Implementation of RedditAuthApi using Ktor
 */
class RedditAuthApiImpl(
    private val client: HttpClient
) : RedditAuthApi {

    companion object {
        private const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com"
        private const val CLIENT_ID = "NwwBZLNAZdQKYF_m5L2yvw"
        private const val CLIENT_SECRET = "mxHR7HyfTxq5aKt9TTewniNzbILsoA"
        const val REFRESH_TOKEN = "1788215293567-ykLZrL5Yh691N_RRhFGMfPLd05zHXw"
    }

    override suspend fun refreshToken(
        grantType: String,
        refreshToken: String
    ): TokenResponse {
        val credentials = "$CLIENT_ID:$CLIENT_SECRET"
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(), 
            android.util.Base64.DEFAULT
        ).trim()

        return client.post("$REDDIT_AUTH_BASE_URL/api/v1/access_token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("grant_type=$grantType&refresh_token=$refreshToken")
            headers.append("Authorization", "Basic $encodedCredentials")
            headers.append("User-Agent", "SubStream-Android/1.0")
        }.body()
    }
}



/**
 * Token response from Reddit API
 */
@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("token_type") val tokenType: String,
    @SerialName("scope") val scope: String,
    @SerialName("refresh_token") val refreshToken: String? = null
)