package com.rookie.code.substream.data.ktor

import android.content.Context
import android.content.Intent
import android.widget.Toast

object NetworkProfiler {
    
    /**
     * Open the OkHttp Profiler to view network requests
     * This will show all HTTP requests made by your app including Reddit API calls
     */
    fun openProfiler(context: Context) {
        try {
            val profilerClass = Class.forName("com.localebro.okhttpprofiler.OkHttpProfilerInterceptor")
            val intent = Intent(context, profilerClass)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Profiler not available in release builds", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Check if profiler is available (only in debug builds)
     */
    fun isProfilerAvailable(): Boolean {
        return try {
            Class.forName("com.localebro.okhttpprofiler.OkHttpProfilerInterceptor")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}
