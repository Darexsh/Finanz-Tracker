package com.darexsh.finanztracker.ui

import java.util.Locale

object FinanceLabelLocalizer {
    private val categoryDeToEn = mapOf(
        "Miete" to "Rent",
        "Nebenkosten" to "Additional costs",
        "Strom/Gas" to "Electricity/Gas",
        "Internet/Handy" to "Internet/Phone",
        "Lebensmittel" to "Groceries",
        "Drogerie" to "Drugstore",
        "Haushalt" to "Household",
        "Mobilität" to "Mobility",
        "Auto" to "Car",
        "Parken" to "Parking",
        "ÖPNV" to "Public transport",
        "Versicherung" to "Insurance",
        "Abgaben/Beiträge" to "Fees/Contributions",
        "Gesundheit" to "Health",
        "Shopping" to "Shopping",
        "Kleidung" to "Clothing",
        "Elektronik" to "Electronics",
        "Freizeit" to "Leisure",
        "Gaming/Medien" to "Gaming/Media",
        "Gastronomie" to "Dining",
        "Reisen" to "Travel",
        "Bildung" to "Education",
        "Geschenke" to "Gifts",
        "Kinder" to "Children",
        "Haustiere" to "Pets",
        "Abo" to "Subscription",
        "Steuern/Gebühren" to "Taxes/Fees",
        "Gehalt" to "Salary",
        "Nebenverdienst" to "Side income",
        "Transfer" to "Transfer",
        "Sonstiges" to "Other"
    )

    private val accountDeToEn = mapOf(
        "Girokonto" to "Checking Account",
        "Kreditkarte" to "Credit Card",
        "Paypal" to "PayPal",
        "Bargeld" to "Cash",
        "Extra Konto" to "Extra Account",
        "Sonstiges" to "Other"
    )

    private val categoryEnToDe = categoryDeToEn.entries.associate { (de, en) ->
        en.lowercase(Locale.ROOT) to de
    }
    private val accountEnToDe = accountDeToEn.entries.associate { (de, en) ->
        en.lowercase(Locale.ROOT) to de
    }

    fun localizeCategory(value: String, locale: Locale = Locale.getDefault()): String {
        if (value.isBlank()) return value
        return if (locale.language.equals("de", ignoreCase = true)) {
            categoryEnToDe[value.lowercase(Locale.ROOT)] ?: value
        } else {
            categoryDeToEn[value] ?: value
        }
    }

    fun localizeAccount(value: String, locale: Locale = Locale.getDefault()): String {
        if (value.isBlank()) return value
        return if (locale.language.equals("de", ignoreCase = true)) {
            accountEnToDe[value.lowercase(Locale.ROOT)] ?: value
        } else {
            accountDeToEn[value] ?: value
        }
    }
}
