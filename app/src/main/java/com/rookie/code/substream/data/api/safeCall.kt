package com.rookie.code.substream.data.api

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * Safe call extension function for HttpClient
 * Follows the same pattern as the example ProfileApi
 */
suspend inline fun <reified T> safeCall(
    crossinline apiCall: suspend () -> HttpResponse
): Resource<T> {
    return try {
        val response = apiCall()
        val data: T = response.body<T>()
        Resource.Success(data)
    } catch (e: Exception) {
        Resource.Error(e)
    }
}
