package com.rookie.code.substream.di

import com.rookie.code.substream.presentation.viewmodel.SubredditViewModel
import com.rookie.code.substream.presentation.viewmodel.VideoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    
    // ViewModels
    viewModel {
        SubredditViewModel(get())
    }
    
    viewModel {
        VideoViewModel(get())
    }
}

fun initKoin(androidContext: android.content.Context) {
    startKoin {
        androidContext(androidContext)
        modules(
            appModule,
            networkModule
        )
    }
}
