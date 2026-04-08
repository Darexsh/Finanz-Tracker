package com.darexsh.finanztracker.domain

object FinanceCatalog {
    val categories = listOf(
        "Miete", "Nebenkosten", "Strom/Gas", "Internet/Handy", "Lebensmittel", "Drogerie",
        "Haushalt", "Mobilität", "Auto", "Parken", "ÖPNV", "Versicherung", "Abgaben/Beiträge",
        "Gesundheit", "Shopping", "Kleidung", "Elektronik", "Freizeit", "Gaming/Medien",
        "Gastronomie", "Reisen", "Bildung", "Geschenke", "Kinder", "Haustiere", "Abo",
        "Steuern/Gebühren", "Gehalt", "Nebenverdienst", "Transfer", "Sonstiges"
    )

    val accounts = listOf(
        "Girokonto", "Kreditkarte", "Paypal", "Bargeld", "Extra Konto", "Sonstiges"
    )

    private val keywordMap = listOf(
        "miete" to "Miete",
        "nebenkosten" to "Nebenkosten",
        "strom" to "Strom/Gas",
        "gas" to "Strom/Gas",
        "simon" to "Internet/Handy",
        "simon mobile" to "Internet/Handy",
        "internet" to "Internet/Handy",
        "handy" to "Internet/Handy",
        "lidl" to "Lebensmittel",
        "aldi" to "Lebensmittel",
        "rewe" to "Lebensmittel",
        "edeka" to "Lebensmittel",
        "einkauf" to "Lebensmittel",
        "dm" to "Drogerie",
        "rossmann" to "Drogerie",
        "nagellack" to "Drogerie",
        "entfetter" to "Drogerie",
        "staubsauger" to "Haushalt",
        "schrauben" to "Haushalt",
        "regenschirm" to "Haushalt",
        "backfolie" to "Haushalt",
        "backofenlampe" to "Haushalt",
        "batterien" to "Haushalt",
        "teelicht" to "Haushalt",
        "teelichter" to "Haushalt",
        "kerze" to "Haushalt",
        "kerzen" to "Haushalt",
        "bahn" to "ÖPNV",
        "deutschlandticket" to "ÖPNV",
        "tanken" to "Auto",
        "tank" to "Auto",
        "benzin" to "Auto",
        "aral" to "Auto",
        "parken" to "Parken",
        "versicherung" to "Versicherung",
        "rechtsschutz" to "Versicherung",
        "adac" to "Versicherung",
        "zahnzusatz" to "Versicherung",
        "auslandskrankenversicherung" to "Versicherung",
        "gez" to "Abgaben/Beiträge",
        "rundfunk" to "Abgaben/Beiträge",
        "arzt" to "Gesundheit",
        "apotheke" to "Gesundheit",
        "zahn" to "Gesundheit",
        "temu" to "Shopping",
        "shein" to "Shopping",
        "aliexpress" to "Shopping",
        "banggood" to "Shopping",
        "tedi" to "Shopping",
        "action" to "Shopping",
        "amazon" to "Shopping",
        "socken" to "Kleidung",
        "schuhe" to "Kleidung",
        "jacke" to "Kleidung",
        "winterjacke" to "Kleidung",
        "pc" to "Elektronik",
        "cpu" to "Elektronik",
        "kühler" to "Elektronik",
        "splitter" to "Elektronik",
        "sata" to "Elektronik",
        "tapo" to "Elektronik",
        "etikettierer" to "Elektronik",
        "solo leveling" to "Gaming/Medien",
        "geisterakten" to "Gaming/Medien",
        "too good to go" to "Gastronomie",
        "burger king" to "Gastronomie",
        "mcdonald" to "Gastronomie",
        "essen" to "Gastronomie",
        "schaschlik" to "Gastronomie",
        "holy" to "Gastronomie",
        "urlaub" to "Reisen",
        "geschenk" to "Geschenke",
        "netflix" to "Abo",
        "spotify" to "Abo",
        "chatgpt" to "Abo",
        "chatgpt plus" to "Abo",
        "gehalt" to "Gehalt",
        "arbeit" to "Gehalt",
        "extra konto" to "Transfer",
        "paypal" to "Transfer"
    )

    private val categoryAliasMap = mapOf(
        "abgabe" to "Abgaben/Beiträge",
        "abgaben" to "Abgaben/Beiträge",
        "beitrag" to "Abgaben/Beiträge",
        "beitraege" to "Abgaben/Beiträge",
        "beiträge" to "Abgaben/Beiträge",
        "beitrage" to "Abgaben/Beiträge",
        "gebuhr" to "Steuern/Gebühren",
        "gebuehr" to "Steuern/Gebühren",
        "gebühr" to "Steuern/Gebühren",
        "gebuhren" to "Steuern/Gebühren",
        "gebuehren" to "Steuern/Gebühren",
        "gebühren" to "Steuern/Gebühren",
        "sonstige" to "Sonstiges",
        "other" to "Sonstiges"
    )

    fun suggestCategory(description: String, availableCategories: List<String>): String? {
        val normalized = description.trim().lowercase()
        if (normalized.isBlank()) return null
        val mapped = keywordMap.firstOrNull { (keyword, _) -> normalized.contains(keyword) }?.second ?: return null
        return availableCategories.firstOrNull { it.equals(mapped, ignoreCase = true) } ?: mapped
    }

    fun normalizeCategory(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return "Sonstiges"

        val key = trimmed
            .lowercase()
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")

        return categoryAliasMap[key] ?: trimmed
    }
}
