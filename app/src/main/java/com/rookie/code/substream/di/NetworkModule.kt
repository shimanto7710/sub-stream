package com.rookie.code.substream.di

import com.rookie.code.substream.data.api.RedditApi
import com.rookie.code.substream.data.api.RedditAuthApi
import com.rookie.code.substream.data.api.RedditAuthApiImpl
import com.rookie.code.substream.data.api.SessionManager
import com.rookie.code.substream.data.api.TokenManager
import com.rookie.code.substream.data.repository.SubredditRepositoryImpl
import com.rookie.code.substream.domain.repository.SubredditRepository
import com.rookie.code.substream.data.ktor.KtorClient
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    
    // Initialize SessionManager
    single {
        SessionManager.initialize(androidContext())
        SessionManager
    }
    
    // Reddit Auth API client (for token refresh)
    single<HttpClient>(qualifier = named("authClient")) {
        KtorClient.createAuthClient(androidContext())
    }
    
    // Reddit Auth API
    single<RedditAuthApi> {
        RedditAuthApiImpl(get(qualifier = named("authClient")))
    }
    
    // Token Manager
    single<TokenManager> {
        TokenManager(get())
    }
    
    // Reddit API client with automatic auth
    single<HttpClient>(qualifier = named("redditClient")) {
        KtorClient.createRedditClientSync(
            context = androidContext(),
            redditAuthApi = get(),
            tokenManager = get()
        )
    }
    
    // Reddit API
    single<RedditApi> {
        RedditApi(get(qualifier = named("redditClient")))
    }
    
    // Repository
    single<SubredditRepository> {
        SubredditRepositoryImpl(get())
    }
}
