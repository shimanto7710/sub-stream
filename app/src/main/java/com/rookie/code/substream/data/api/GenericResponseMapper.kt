package com.rookie.code.substream.data.api

import com.rookie.code.substream.data.api.Resource

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> Resource.Error(this.exception)
        is Resource.Loading -> Resource.Loading
    }
}