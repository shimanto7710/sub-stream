package com.rookie.code.substream.di

import com.rookie.code.substream.presentation.viewmodel.SubredditViewModel
import com.rookie.code.substream.presentation.viewmodel.VideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SubredditViewModel(get()) }
    viewModel { VideoViewModel(get()) }
}
