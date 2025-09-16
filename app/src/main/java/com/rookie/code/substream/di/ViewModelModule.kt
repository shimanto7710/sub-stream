package com.rookie.code.substream.di

import com.rookie.code.substream.data.repository.RedditPostsRepositoryImpl
import com.rookie.code.substream.data.repository.SubredditRepositoryImpl
import com.rookie.code.substream.domain.repository.RedditPostsRepository
import com.rookie.code.substream.domain.repository.SubredditRepository
import com.rookie.code.substream.domain.usecase.GetPopularSubredditsUseCase
import com.rookie.code.substream.domain.usecase.GetSubredditPostsUseCase
import com.rookie.code.substream.domain.usecase.SearchSubredditsUseCase
import com.rookie.code.substream.presentation.viewmodel.PostsViewModel
import com.rookie.code.substream.presentation.viewmodel.SubredditViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Repository bindings
    single<SubredditRepository> { SubredditRepositoryImpl(get()) }
    single<RedditPostsRepository> { RedditPostsRepositoryImpl(get()) }
    
    // Use case bindings
    single { GetPopularSubredditsUseCase(get()) }
    single { SearchSubredditsUseCase(get()) }
    single { GetSubredditPostsUseCase(get()) }
    
    // ViewModel bindings
    viewModel { SubredditViewModel(get(), get()) }
    viewModel { PostsViewModel(get()) }
}