package com.darexsh.finanztracker.domain

import com.darexsh.finanztracker.model.TrackerState

enum class CategoryMutationStatus {
    SUCCESS,
    EMPTY_NAME,
    ALREADY_EXISTS,
    BUILT_IN_BLOCKED,
    NOT_FOUND
}

data class CategoryMutationResult(
    val status: CategoryMutationStatus,
    val state: TrackerState? = null
)
