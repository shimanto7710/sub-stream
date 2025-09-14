package com.rookie.code.substream.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    // ViewModels are defined in viewModelModule
}

fun initKoin(androidContext: android.content.Context) {
    startKoin {
        androidContext(androidContext)
        modules(
            appModule,
            networkModule,
            viewModelModule
        )
    }
}
