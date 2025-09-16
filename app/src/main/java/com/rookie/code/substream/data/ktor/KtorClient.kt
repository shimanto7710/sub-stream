package com.rookie.code.substream.data.ktor

import android.content.Context
import com.rookie.code.substream.data.utils.SessionManager
import com.rookie.code.substream.data.online.RedditAuthApi
import com.rookie.code.substream.data.utils.TokenManager
import com.rookie.code.substream.data.online.RedditAuthApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

import com.rookie.code.substream.data.constants.DataConstants

private const val TAG = DataConstants.LogTags.KTOR_CLIENT

object KtorClient {

    // API Configuration Constants
    object ApiConfig {
        const val REQUEST_TIMEOUT = DataConstants.KtorConfig.REQUEST_TIMEOUT
        const val CONNECT_TIMEOUT = DataConstants.KtorConfig.CONNECT_TIMEOUT
        const val READ_TIMEOUT = DataConstants.KtorConfig.READ_TIMEOUT
        const val USER_AGENT = DataConstants.App.USER_AGENT
        const val REDDIT_BASE_URL = DataConstants.Api.REDDIT_BASE_URL
        const val REDDIT_AUTH_BASE_URL = DataConstants.Api.REDDIT_AUTH_BASE_URL
        const val CLIENT_ID = DataConstants.Api.CLIENT_ID
        const val CLIENT_SECRET = DataConstants.Api.CLIENT_SECRET
        const val REFRESH_TOKEN = DataConstants.Api.REFRESH_TOKEN
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    private val refreshMutex = Mutex()


    fun createRedditClientSync(
        context: Context,
        redditAuthApi: RedditAuthApi,
        tokenManager: TokenManager,
        isDebug: Boolean = true
    ): HttpClient {
        // Initialize SessionManager
        SessionManager.initialize(context)
        
        // Initialize refresh token for automatic auth
        SessionManager.updateSession("", RedditAuthApiImpl.REFRESH_TOKEN, 0)
        
        log("createRedditClientSync: accessToken=${SessionManager.getAccessToken()?.take(10)}..., refreshToken=${SessionManager.getRefreshToken()?.take(10)}...")
        
        // Force initial token refresh if no access token
        if (SessionManager.getAccessToken().isNullOrEmpty()) {
            log("createRedditClientSync: No access token, performing initial refresh...")
            runBlocking {
                try {
                    val result = redditAuthApi.refreshToken(
                        grantType = "refresh_token",
                        refreshToken = RedditAuthApiImpl.REFRESH_TOKEN
                    )
                    SessionManager.updateSession(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken ?: RedditAuthApiImpl.REFRESH_TOKEN,
                        expiresIn = result.expiresIn
                    )
                    log("createRedditClientSync: Initial token refresh successful: ${result.accessToken.take(10)}...")
                } catch (e: Exception) {
                    log("createRedditClientSync: Initial token refresh failed: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
        
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
                        val accessToken = SessionManager.getAccessToken()
                        val refreshToken = SessionManager.getRefreshToken()
                        log("loadTokens: accessToken=${accessToken?.take(10)}..., refreshToken=${refreshToken?.take(10)}...")
                        
                        if (accessToken.isNullOrEmpty()) {
                            log("loadTokens: No access token found, will trigger refresh")
                        }
                        
                        BearerTokens(accessToken ?: "", refreshToken ?: "")
                    }

                    refreshTokens {
                        // This function is called when the client receives a 401
                        // Unauthorized response and needs to refresh the tokens.
                        log("refreshTokens dsl: called")

                        // Ensure only one refresh happens at a time.
                        refreshMutex.withLock {
                            log("refreshTokens: starting token refresh...")
                            
                            try {
                                // Call RedditAuthApi directly for token refresh
                                val result = redditAuthApi.refreshToken(
                                    grantType = "refresh_token",
                                    refreshToken = SessionManager.getRefreshToken() ?: RedditAuthApiImpl.REFRESH_TOKEN
                                )
                                
                                log("refreshTokens: API call successful, updating session...")
                                
                                // Update session with new tokens
                                SessionManager.updateSession(
                                    accessToken = result.accessToken,
                                    refreshToken = result.refreshToken ?: SessionManager.getRefreshToken() ?: RedditAuthApiImpl.REFRESH_TOKEN,
                                    expiresIn = result.expiresIn
                                )
                                
                                val newAccess = SessionManager.getAccessToken()
                                val newRefresh = SessionManager.getRefreshToken()
                                log("refreshTokens: newAccess=${newAccess?.take(10)}..., newRefresh=${newRefresh?.take(10)}...")
                                
                                if (!newAccess.isNullOrEmpty()) {
                                    BearerTokens(newAccess, newRefresh ?: "")
                                } else {
                                    log("refreshTokens: newAccess is null or empty")
                                    null
                                }
                            } catch (e: Exception) {
                                log("refreshTokens: Exception during token refresh: ${e.message}")
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                }
            }
        }
    }


    fun createAuthClient(
        context: Context,
        isDebug: Boolean = true
    ): HttpClient {
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
                contentType(ContentType.Application.FormUrlEncoded)
                url {
                    takeFrom(ApiConfig.REDDIT_AUTH_BASE_URL)
                }
                headers.append("User-Agent", ApiConfig.USER_AGENT)
            }
        }
    }


    private fun HttpClientConfig<*>.installCommonPlugins() {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            socketTimeoutMillis = ApiConfig.READ_TIMEOUT
            requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) = println(" [Ktor] $message")
            }
            level = LogLevel.ALL
        }
    }


    fun log(msg: String?) {
        println("$TAG: $msg")
    }
}