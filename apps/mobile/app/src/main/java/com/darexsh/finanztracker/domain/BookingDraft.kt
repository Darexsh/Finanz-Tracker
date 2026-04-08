package com.darexsh.finanztracker.domain

import com.darexsh.finanztracker.model.TxType

data class BookingDraft(
    val date: String,
    val description: String,
    val amount: Double,
    val category: String,
    val txType: TxType,
    val account: String,
    val note: String,
    val taxDeclaration: Boolean
)
