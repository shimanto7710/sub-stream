package com.rookie.code.substream.di

import com.rookie.code.substream.data.api.RedditApi
import com.rookie.code.substream.data.repository.SubredditRepositoryImpl
import com.rookie.code.substream.domain.repository.SubredditRepository
import com.rookie.code.substream.data.ktor.KtorClient
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    
    // Reddit API client with automatic auth
    single<HttpClient> {
        // Note: This will be initialized when first accessed
        // The actual token refresh happens in the first API call
        KtorClient.createRedditClientSync(androidContext())
    }
    
    // Reddit API
    single<RedditApi> {
        RedditApi(get())
    }
    
    // Repository
    single<SubredditRepository> {
        SubredditRepositoryImpl(get())
    }
}
