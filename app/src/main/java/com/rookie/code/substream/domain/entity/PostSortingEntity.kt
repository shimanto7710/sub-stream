package com.rookie.code.substream.domain.entity

import com.rookie.code.substream.data.constants.DataConstants

enum class PostSortingEntity(val displayName: String, val apiValue: String) {
    HOT("Hot", DataConstants.Api.DEFAULT_SORTING),
    NEW("New", "new"),
    TOP("Top", "top"),
    RISING("Rising", "rising")
}
