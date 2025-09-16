package com.rookie.code.substream.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.rookie.code.substream.data.constants.DataConstants
import com.rookie.code.substream.data.online.RedditAuthApiImpl

/**
 * Manages Reddit API session tokens using SharedPreferences
 */
object SessionManager {
    
    private const val PREFS_NAME = DataConstants.PrefsKeys.PREFS_NAME
    private const val KEY_ACCESS_TOKEN = DataConstants.PrefsKeys.KEY_ACCESS_TOKEN
    private const val KEY_REFRESH_TOKEN = DataConstants.PrefsKeys.KEY_REFRESH_TOKEN
    private const val KEY_TOKEN_EXPIRY = DataConstants.PrefsKeys.KEY_TOKEN_EXPIRY
    
    private lateinit var prefs: SharedPreferences
    
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, RedditAuthApiImpl.REFRESH_TOKEN)
    }
    
    fun getTokenExpiry(): Long {
        return prefs.getLong(KEY_TOKEN_EXPIRY, 0L)
    }
    
    fun updateSession(accessToken: String, refreshToken: String?, expiresIn: Int = 3600) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, accessToken)
        refreshToken?.let { editor.putString(KEY_REFRESH_TOKEN, it) }
        editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (expiresIn * 1000L))
        editor.apply()
    }
    
    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(KEY_ACCESS_TOKEN)
        editor.remove(KEY_REFRESH_TOKEN)
        editor.remove(KEY_TOKEN_EXPIRY)
        editor.apply()
    }
    
    fun isTokenValid(): Boolean {
        val token = getAccessToken()
        val expiry = getTokenExpiry()
        return !token.isNullOrEmpty() && System.currentTimeMillis() < expiry
    }
}
