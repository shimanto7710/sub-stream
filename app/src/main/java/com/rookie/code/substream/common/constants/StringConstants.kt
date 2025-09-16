package com.rookie.code.substream.common.constants

object StringConstants {

    // App Information
    object App {
        const val APP_NAME = "SubStream"
        const val USER_AGENT = "SubStream-Android/1.0"
        const val VERSION = "1.0"
    }

    // API Configuration
    object Api {
        const val REDDIT_BASE_URL = "https://oauth.reddit.com"
        const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com"
        const val CLIENT_ID = "NwwBZLNAZdQKYF_m5L2yvw"
        const val CLIENT_SECRET = "mxHR7HyfTxq5aKt9TTewniNzbILsoA"
        const val REFRESH_TOKEN = "1788215293567-ykLZrL5Yh691N_RRhFGMfPLd05zHXw"
        
        // Endpoints
        const val ENDPOINT_POPULAR_SUBREDDITS = "subreddits/popular.json"
        const val ENDPOINT_SEARCH_SUBREDDITS = "subreddits/search"
        const val ENDPOINT_SUBREDDIT_POSTS = "r/{subreddit}/{sorting}.json"
        
        // Parameters
        const val PARAM_LIMIT = "limit"
        const val PARAM_AFTER = "after"
        const val PARAM_QUERY = "q"
        const val PARAM_TYPE = "type"
        const val PARAM_TYPE_SUBREDDIT = "sr"
        
        // Default values
        const val DEFAULT_LIMIT = 25
        const val DEFAULT_SORTING = "hot"
    }

    // App-wide UI Text (used across multiple screens)
    object UiText {
        // Sorting Options (used in multiple screens)
        const val SORT_HOT = "Hot"
        const val SORT_NEW = "New"
        const val SORT_TOP = "Top"
        const val SORT_RISING = "Rising"
    }

    // App-wide Logging Tags (used across multiple files)
    object LogTags {
        const val KTOR_CLIENT = "KtorClient"
        const val TOKEN_MANAGER = "TokenManager"
        const val SESSION_MANAGER = "SessionManager"
    }

    // SharedPreferences Keys
    object PrefsKeys {
        const val PREFS_NAME = "reddit_session"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    // App-wide Error Messages (used across multiple files)
    object ErrorMessages {
        const val NO_REFRESH_TOKEN = "No refresh token available"
        const val NO_ACCESS_TOKEN = "No access token available"
    }
}
