package com.darexsh.finanztracker.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackerUser(
    val id: String,
    val name: String
)

@Serializable
enum class TxType {
    EXPENSE,
    INCOME
}

@Serializable
data class Booking(
    val id: String,
    val userId: String,
    val date: String,
    val description: String,
    val category: String,
    val txType: TxType,
    val amount: Double,
    val account: String,
    val note: String = "",
    val taxDeclaration: Boolean = false,
    val createdAt: Long
)

@Serializable
data class TrackerState(
    val users: List<TrackerUser>,
    val activeUserId: String,
    val bookings: List<Booking>,
    val customCategories: List<String>
)

fun defaultState(): TrackerState {
    val user = TrackerUser(id = "default", name = "Standard")
    return TrackerState(
        users = listOf(user),
        activeUserId = user.id,
        bookings = emptyList(),
        customCategories = emptyList()
    )
}
