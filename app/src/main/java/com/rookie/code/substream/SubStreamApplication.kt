package com.rookie.code.substream

import android.app.Application
import com.rookie.code.substream.di.initKoin

/**
 * Application class for SubStream
 * Handles app-wide initialization including dependency injection
 */
class SubStreamApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin dependency injection
        initKoin(this)
    }
}
