# Reddit API Integration with Automatic Token Refresh

This project implements a clean architecture solution for Reddit API integration with automatic token refresh using Ktor, Koin, and Kotlin Coroutines.

## Features

- ✅ Automatic token refresh when expired
- ✅ Proactive token refresh (5 minutes before expiry)
- ✅ Thread-safe token management
- ✅ Clean architecture with separation of concerns
- ✅ Dependency injection with Koin
- ✅ Ktor HTTP client with interceptors
- ✅ Kotlin Serialization for JSON handling

## Architecture

```
├── data/
│   ├── api/           # API interfaces and implementations
│   ├── model/         # Data models
│   └── repository/    # Repository implementations
├── domain/
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Use cases
├── di/               # Dependency injection modules
└── util/             # Utilities (interceptors, etc.)
```

## Setup

### 1. Dependencies

The following dependencies are already configured in `build.gradle.kts`:

- Ktor Client (2.3.12)
- Koin (3.5.6)
- Kotlin Serialization (1.7.3)
- Kotlin Coroutines (1.8.1)

### 2. Initialize Koin

In your `Application` class or `MainActivity`:

```kotlin
import com.rookie.code.substream.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Koin
        initKoin(this)
        
        // ... rest of your code
    }
}
```

## Usage

### 1. Set Refresh Token

First, you need to set your Reddit refresh token:

```kotlin
class MainActivity : ComponentActivity() {
    private val redditRepository: RedditRepository by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        initKoin(this)
        
        // Set your refresh token
        redditRepository.setRefreshToken("your_refresh_token_here")
        
        // ... rest of your code
    }
}
```

### 2. Make API Calls

The token will be automatically added to all Reddit API requests:

```kotlin
class YourViewModel : ViewModel() {
    private val redditApi: RedditApi by inject()
    
    fun fetchPosts() {
        viewModelScope.launch {
            val result = redditApi.getSubredditPosts("android", limit = 25)
            
            result.fold(
                onSuccess = { response ->
                    // Handle successful response
                    println("Posts: $response")
                },
                onFailure = { error ->
                    // Handle error
                    println("Error: ${error.message}")
                }
            )
        }
    }
}
```

### 3. Manual Token Management

You can also manually manage tokens:

```kotlin
class YourViewModel : ViewModel() {
    private val refreshTokenUseCase: RefreshTokenUseCase by inject()
    private val getValidTokenUseCase: GetValidTokenUseCase by inject()
    
    fun refreshToken() {
        viewModelScope.launch {
            val result = refreshTokenUseCase()
            result.fold(
                onSuccess = { token ->
                    println("New token: ${token.take(10)}...")
                },
                onFailure = { error ->
                    println("Refresh failed: ${error.message}")
                }
            )
        }
    }
    
    fun getCurrentToken() {
        viewModelScope.launch {
            val token = getValidTokenUseCase()
            println("Current token: ${token?.take(10)}...")
        }
    }
}
```

## API Endpoints

### Token Refresh

- **Endpoint**: `https://www.reddit.com/api/v1/access_token`
- **Method**: POST
- **Body**: `grant_type=refresh_token&refresh_token=YOUR_REFRESH_TOKEN`
- **Headers**: `User-Agent: SubStream/1.0 by YourUsername`

### Reddit API

- **Base URL**: `https://oauth.reddit.com`
- **Authentication**: Bearer token (automatically added by interceptor)

## Token Management

The `TokenManager` handles:

1. **Automatic Refresh**: Tokens are refreshed when expired
2. **Proactive Refresh**: Tokens are refreshed 5 minutes before expiry
3. **Thread Safety**: All operations are thread-safe using Mutex
4. **Caching**: Valid tokens are cached to avoid unnecessary API calls

## Error Handling

The implementation includes comprehensive error handling:

- Network errors
- Token refresh failures
- Invalid responses
- Timeout errors

## Example Implementation

See `RedditApiUsageExample.kt` for a complete example of how to use the Reddit API with automatic token refresh.

## Security Notes

1. **Never commit refresh tokens to version control**
2. **Store refresh tokens securely** (e.g., Android Keystore)
3. **Use HTTPS for all API calls**
4. **Implement proper error handling and logging**

## Testing

To test the implementation:

1. Set a valid Reddit refresh token
2. Make API calls to Reddit endpoints
3. Verify that tokens are automatically refreshed
4. Check logs for token refresh events

## Troubleshooting

### Common Issues

1. **"No refresh token available"**: Make sure to call `setRefreshToken()` before making API calls
2. **401 Unauthorized**: Check if your refresh token is valid
3. **Network errors**: Verify internet connectivity and Reddit API status

### Debug Logging

Enable debug logging in the Ktor client configuration to see detailed request/response information.
