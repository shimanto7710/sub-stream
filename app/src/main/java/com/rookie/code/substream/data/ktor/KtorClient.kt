package com.rookie.code.substream.data.ktor

import android.content.Context
import com.rookie.code.substream.data.api.ResultRefreshToken
import com.rookie.code.substream.data.api.NetworkSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private const val TAG = "KtorClient"

object KtorClient {

    // API Configuration Constants
    object ApiConfig {
        const val REQUEST_TIMEOUT = 30_000L // 30 seconds
        const val CONNECT_TIMEOUT = 10_000L // 10 seconds
        const val READ_TIMEOUT = 30_000L    // 30 seconds
        const val USER_AGENT = "SubStream-Android/1.0"
        const val REDDIT_BASE_URL = "https://oauth.reddit.com"
        const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com"
        const val CLIENT_ID = "NwwBZLNAZdQKYF_m5L2yvw"
        const val CLIENT_SECRET = "mxHR7HyfTxq5aKt9TTewniNzbILsoA"
        const val REFRESH_TOKEN = "1788215293567-ykLZrL5Yh691N_RRhFGMfPLd05zHXw"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    private val refreshMutex = Mutex()

    /**
     * Creates a Reddit API client with automatic token refresh
     * This is the main client for Reddit API calls
     */
    fun createRedditClientSync(
        context: Context,
        isDebug: Boolean = true
    ): HttpClient {
        // Initialize SessionManager
        NetworkSessionManager.initialize(context)
        
        // Initialize refresh token for automatic auth
        NetworkSessionManager.updateSession("", ApiConfig.REFRESH_TOKEN, 0)
        
        log("createRedditClientSync: accessToken=${NetworkSessionManager.getAccessToken()?.take(10)}..., refreshToken=${NetworkSessionManager.getRefreshToken()?.take(10)}...")
        
        // The Ktor Auth plugin will handle token refresh automatically
        // when it receives a 401 response

        // Create OkHttp client with profiler (debug only)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .apply {
                // Add profiler only in debug builds
                if (isDebug) {
                    try {
                        val profilerClass = Class.forName("com.localebro.okhttpprofiler.OkHttpProfilerInterceptor")
                        val profilerInstance = profilerClass.getDeclaredConstructor().newInstance()
                        addInterceptor(profilerInstance as okhttp3.Interceptor)
                    } catch (e: Exception) {
                        // Profiler not available in release builds
                    }
                }
            }
            .build()

        return HttpClient(OkHttp) {
            // Use preconfigured OkHttp client
            engine {
                preconfigured = okHttpClient
            }

            installCommonPlugins()

            defaultRequest {
                contentType(ContentType.Application.Json)
                url {
                    takeFrom(ApiConfig.REDDIT_BASE_URL)
                }
                headers.append("User-Agent", ApiConfig.USER_AGENT)
                headers.append("Accept", "application/json")
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        // Load existing tokens from storage
                        val accessToken = NetworkSessionManager.getAccessToken()
                        val refreshToken = NetworkSessionManager.getRefreshToken()
                        log("loadTokens: accessToken=${accessToken?.take(10)}..., refreshToken=${refreshToken?.take(10)}...")
                        BearerTokens(accessToken ?: "", refreshToken ?: "")
                    }

                    refreshTokens {
                        // This function is called when the client receives a 401
                        // Unauthorized response and needs to refresh the tokens.
                        log("refreshTokens dsl: called")

                        // Ensure only one refresh happens at a time.
                        refreshMutex.withLock {
                            log("refreshTokens: starting token refresh...")
                            val result = refreshToken(OkHttp.create { preconfigured = okHttpClient })
                            log("refreshTokens inside mutex: $result")
                            if (result.isSuccess) {
                                val newAccess = NetworkSessionManager.getAccessToken()
                                val newRefresh = NetworkSessionManager.getRefreshToken()
                                log("refreshTokens: newAccess=${newAccess?.take(10)}..., newRefresh=${newRefresh?.take(10)}...")
                                if (!newAccess.isNullOrEmpty()) {
                                    BearerTokens(newAccess, newRefresh ?: "")
                                } else {
                                    log("refreshTokens: newAccess is null or empty")
                                    null
                                }
                            } else {
                                // Optional: logout or clear session if refresh fails
                                log("refreshTokens: failed to refresh tokens")
                                onTokenRefreshFailed(result.responseCode)
                                null
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Common configuration shared by all clients.
     */
    private fun HttpClientConfig<*>.installCommonPlugins() {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            socketTimeoutMillis = ApiConfig.READ_TIMEOUT
            requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) = println("🌐 [Ktor] $message")
            }
            level = LogLevel.ALL
        }
    }

    /**
     * Refreshes the Reddit access token using the refresh token
     */
    suspend fun refreshToken(engine: HttpClientEngine): ResultRefreshToken {
        log("refreshToken called")
        val refreshToken = NetworkSessionManager.getRefreshToken() ?: return ResultRefreshToken(
            responseCode = -1,
            isSuccess = false
        )

        val refreshClient = createRefreshClient(engine)

        return try {
            val credentials = "${ApiConfig.CLIENT_ID}:${ApiConfig.CLIENT_SECRET}"
            val encodedCredentials = android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.DEFAULT).trim()

            val response: HttpResponse = refreshClient.post(
                "${ApiConfig.REDDIT_AUTH_BASE_URL}/api/v1/access_token"
            ) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("grant_type=refresh_token&refresh_token=$refreshToken")
                headers.append("Authorization", "Basic $encodedCredentials")
                headers.append("User-Agent", ApiConfig.USER_AGENT)
            }

            log("refreshToken: $response")

            if (response.status == HttpStatusCode.OK) {
                try {
                    val responseText = response.body<String>()
                    log("refreshToken response text: $responseText")
                    
                    // Parse JSON manually
                    val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    val responseBody = json.decodeFromString<Map<String, kotlinx.serialization.json.JsonElement>>(responseText)
                    
                    val newAccessToken = responseBody["access_token"]?.toString()?.trim('"')
                    val newRefreshToken = responseBody["refresh_token"]?.toString()?.trim('"')
                    val expiresIn = responseBody["expires_in"]?.toString()?.toIntOrNull() ?: 3600

                log("refreshToken parsed - accessToken: ${newAccessToken?.take(10)}..., refreshToken: ${newRefreshToken?.take(10)}..., expiresIn: $expiresIn")

                if (!newAccessToken.isNullOrBlank()) {
                    NetworkSessionManager.updateSession(newAccessToken, newRefreshToken, expiresIn)
                    log("refreshToken: Session updated successfully")
                    ResultRefreshToken(
                        responseCode = response.status.value,
                        isSuccess = true
                    )
                } else {
                    log("refreshToken: No access token in response")
                    ResultRefreshToken(
                        responseCode = response.status.value,
                        isSuccess = false
                    )
                }
                } catch (e: Exception) {
                    log("refreshToken: Error parsing response body: ${e.message}")
                    e.printStackTrace()
                    ResultRefreshToken(
                        responseCode = response.status.value,
                        isSuccess = false
                    )
                }
            } else {
                log("refreshToken: Non-200 response: ${response.status}")
                ResultRefreshToken(
                    responseCode = response.status.value,
                    isSuccess = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultRefreshToken(
                responseCode = -1,
                isSuccess = false
            )
        }
    }

    /**
     * Creates a new HttpClient instance for refreshing tokens.
     * This client is used specifically for token refresh operations.
     * A lightweight client without Auth plugin to avoid recursion
     */
    private fun createRefreshClient(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            installCommonPlugins()

            defaultRequest {
                contentType(ContentType.Application.FormUrlEncoded)
                url {
                    takeFrom(ApiConfig.REDDIT_AUTH_BASE_URL)
                }
                headers.append("User-Agent", ApiConfig.USER_AGENT)
            }
        }
    }

    private fun onTokenRefreshFailed(responseCode: Int) {
        log("onTokenRefreshFailed: $responseCode")
        // Clear session on refresh failure
        NetworkSessionManager.clearSession()
    }

    fun log(msg: String?) {
        println("$TAG: $msg")
    }
}