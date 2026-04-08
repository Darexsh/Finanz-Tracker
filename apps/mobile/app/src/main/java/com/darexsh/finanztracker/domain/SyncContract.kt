package com.darexsh.finanztracker.domain

import com.darexsh.finanztracker.model.TxType

object SyncContract {
    const val SYNC_FILE_NAME = "finanz-tracker-sync-latest.json"

    object Keys {
        const val USERS = "users"
        const val USER_ID = "id"
        const val USER_NAME = "name"
        const val ACTIVE_USER_ID = "activeUserId"
        const val BOOKINGS = "bookings"
        const val BOOKING_ID = "id"
        const val BOOKING_USER_ID = "userId"
        const val BOOKING_DATE = "date"
        const val BOOKING_MONTH = "month"
        const val BOOKING_DESCRIPTION = "description"
        const val BOOKING_CATEGORY = "category"
        const val BOOKING_TX_TYPE = "txType"
        const val BOOKING_AMOUNT = "amount"
        const val BOOKING_ACCOUNT = "account"
        const val BOOKING_NOTE = "note"
        const val BOOKING_TAX_DECLARATION = "taxDeclaration"
        const val BOOKING_CREATED_AT = "createdAt"
        const val CUSTOM_CATEGORIES = "customCategories"
    }

    fun toSyncTxType(txType: TxType): String {
        return if (txType == TxType.INCOME) "Einnahme" else "Ausgabe"
    }

    fun parseTxType(raw: String?): TxType {
        val normalized = raw?.trim()?.lowercase() ?: return TxType.EXPENSE
        return when (normalized) {
            "income", "einnahme" -> TxType.INCOME
            else -> TxType.EXPENSE
        }
    }

    fun normalizeDate(raw: String): String {
        val text = raw.trim()

        val full = Regex("^\\d{2}\\.\\d{2}\\.\\d{4}$")
        if (full.matches(text)) return text

        val short = Regex("^\\d{2}\\.\\d{4}$")
        if (short.matches(text)) return "01.$text"

        val flexible = Regex("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})$")
        val match = flexible.find(text)
        if (match != null) {
            val day = match.groupValues[1].padStart(2, '0')
            val month = match.groupValues[2].padStart(2, '0')
            val year = match.groupValues[3]
            return "$day.$month.$year"
        }

        return text
    }
}
