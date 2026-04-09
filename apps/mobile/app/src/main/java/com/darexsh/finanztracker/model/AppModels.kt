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
enum class AppStartTab {
    OVERVIEW,
    BOOKINGS,
    REPORTS,
    SYNC,
    SETTINGS
}

@Serializable
enum class ExportFormatPreference {
    PDF,
    XLSX,
    CSV
}

@Serializable
enum class LanguagePreference {
    SYSTEM,
    GERMAN,
    ENGLISH
}

@Serializable
enum class DateFormatPreference {
    DMY_DOT, // dd.MM.yyyy
    YMD_DASH, // yyyy-MM-dd
    MDY_SLASH // MM/dd/yyyy
}

@Serializable
enum class CurrencyPreference {
    EUR,
    USD,
    GBP
}

@Serializable
enum class FontSizeMode {
    COMPACT,
    NORMAL,
    LARGE
}

@Serializable
enum class NavigationAnimationStyle {
    SLIDE,
    FADE,
    ZOOM,
    POP,
    ROTATE,
    NONE
}

@Serializable
data class AppSettings(
    val keepDateAfterSave: Boolean = true,
    val reduceAnimations: Boolean = false,
    val startTab: AppStartTab = AppStartTab.OVERVIEW,
    val defaultExportFormat: ExportFormatPreference = ExportFormatPreference.PDF,
    val language: LanguagePreference = LanguagePreference.SYSTEM,
    val dateFormat: DateFormatPreference = DateFormatPreference.DMY_DOT,
    val currency: CurrencyPreference = CurrencyPreference.EUR,
    val bookingsSortNewestFirst: Boolean = true,
    val categorySuggestionsEnabled: Boolean = true,
    val fontSizeMode: FontSizeMode = FontSizeMode.NORMAL,
    val navigationAnimationStyle: NavigationAnimationStyle = NavigationAnimationStyle.SLIDE,
    val appLockEnabled: Boolean = false
)

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
    val customCategories: List<String>,
    val syncFolderUri: String? = null,
    val appSettings: AppSettings = AppSettings()
)

fun defaultState(): TrackerState {
    val user = TrackerUser(id = "default", name = "Standard")
    return TrackerState(
        users = listOf(user),
        activeUserId = user.id,
        bookings = emptyList(),
        customCategories = emptyList(),
        syncFolderUri = null,
        appSettings = AppSettings()
    )
}
