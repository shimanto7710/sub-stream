package com.rookie.code.substream.data.api

/**
 * Resource wrapper for API responses
 * Follows the same pattern as the example ProfileApi
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

/**
 * Extension function to create Resource from Result
 */
fun <T> Result<T>.toResource(): Resource<T> {
    return fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error(it) }
    )
}

/**
 * Extension function to get data from Resource
 */
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to handle error from Resource
 */
inline fun <T> Resource<T>.onError(action: (Throwable) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(exception)
    }
    return this
}

