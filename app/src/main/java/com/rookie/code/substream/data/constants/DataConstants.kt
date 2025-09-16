package com.rookie.code.substream.data.constants

object DataConstants {

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

    // App Information
    object App {
        const val APP_NAME = "SubStream"
        const val USER_AGENT = "SubStream-Android/1.0"
        const val APP_VERSION = "1.0"
    }

    // Logging Tags
    object LogTags {
        const val KTOR_CLIENT = "KtorClient"
        const val TOKEN_MANAGER = "TokenManager"
        const val SESSION_MANAGER = "SessionManager"
        const val REDDIT_API = "RedditApi"
    }

    // SharedPreferences Keys
    object PrefsKeys {
        const val PREFS_NAME = "reddit_session"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    // Error Messages
    object ErrorMessages {
        const val NO_REFRESH_TOKEN = "No refresh token available"
        const val NO_ACCESS_TOKEN = "No access token available"
    }

    // API Messages
    object ApiMessages {
        const val RESPONSE_STATUS = "Response status: {status}"
        const val RESPONSE_HEADERS = "Response headers: {headers}"
        const val CALLING_ENDPOINT = "Calling endpoint: {endpoint} with after: {after}"
    }

    // Ktor Client Configuration
    object KtorConfig {
        const val REQUEST_TIMEOUT = 30_000L // 30 seconds
        const val CONNECT_TIMEOUT = 10_000L // 10 seconds
        const val READ_TIMEOUT = 30_000L    // 30 seconds
    }
}
