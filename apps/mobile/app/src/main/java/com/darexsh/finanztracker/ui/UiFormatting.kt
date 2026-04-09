package com.darexsh.finanztracker.ui

import com.darexsh.finanztracker.model.CurrencyPreference
import com.darexsh.finanztracker.model.DateFormatPreference
import java.util.Locale

fun currencySymbol(pref: CurrencyPreference): String {
    return when (pref) {
        CurrencyPreference.EUR -> "€"
        CurrencyPreference.USD -> "$"
        CurrencyPreference.GBP -> "£"
    }
}

fun formatCurrencyValue(value: Double, pref: CurrencyPreference): String {
    return String.format(Locale.GERMANY, "%.2f %s", value, currencySymbol(pref))
}

fun canonicalDateToDisplay(canonicalDate: String, format: DateFormatPreference): String {
    val (day, month, year) = parseCanonicalDate(canonicalDate) ?: return canonicalDate
    return when (format) {
        DateFormatPreference.DMY_DOT -> "%02d.%02d.%04d".format(Locale.GERMANY, day, month, year)
        DateFormatPreference.YMD_DASH -> "%04d-%02d-%02d".format(Locale.GERMANY, year, month, day)
        DateFormatPreference.MDY_SLASH -> "%02d/%02d/%04d".format(Locale.GERMANY, month, day, year)
    }
}

fun displayDateToCanonical(displayDate: String, format: DateFormatPreference): String? {
    val trimmed = displayDate.trim()
    val parsed = when (format) {
        DateFormatPreference.DMY_DOT -> parseByPattern(trimmed, '.')
        DateFormatPreference.YMD_DASH -> parseYmdDash(trimmed)
        DateFormatPreference.MDY_SLASH -> parseMdySlash(trimmed)
    } ?: return null
    val (day, month, year) = parsed
    if (day !in 1..31 || month !in 1..12 || year !in 1..9999) return null
    return "%02d.%02d.%04d".format(Locale.GERMANY, day, month, year)
}

private fun parseCanonicalDate(canonicalDate: String): Triple<Int, Int, Int>? {
    val parts = canonicalDate.split(".")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return Triple(day, month, year)
}

private fun parseByPattern(value: String, sep: Char): Triple<Int, Int, Int>? {
    val parts = value.split(sep)
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return Triple(day, month, year)
}

private fun parseYmdDash(value: String): Triple<Int, Int, Int>? {
    val parts = value.split("-")
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null
    return Triple(day, month, year)
}

private fun parseMdySlash(value: String): Triple<Int, Int, Int>? {
    val parts = value.split("/")
    if (parts.size != 3) return null
    val month = parts[0].toIntOrNull() ?: return null
    val day = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return Triple(day, month, year)
}

