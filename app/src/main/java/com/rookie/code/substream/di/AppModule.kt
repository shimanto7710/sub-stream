package com.rookie.code.substream.di

import com.rookie.code.substream.presentation.viewmodel.SubredditViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    
    // ViewModel
    viewModel {
        SubredditViewModel(get())
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
