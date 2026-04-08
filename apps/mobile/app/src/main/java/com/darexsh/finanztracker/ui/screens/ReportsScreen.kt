package com.darexsh.finanztracker.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.export.XlsxBuilder
import com.darexsh.finanztracker.domain.export.XlsxSheet
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import java.io.ByteArrayOutputStream
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ReportExportScope(val key: String) {
    SUMMARY("summary"),
    YEAR_COMPARISON("year-comparison"),
    YEAR_BOOKINGS("year-bookings"),
    MONTH_BOOKINGS("month-bookings")
}

private enum class ReportExportFormat(val extension: String) {
    CSV("csv"),
    PDF("pdf"),
    XLSX("xlsx")
}

private data class ExportPayload(
    val filename: String,
    val bytes: ByteArray
)

private data class CompareRow(
    val metric: String,
    val previous: Double,
    val current: Double,
    val isCurrency: Boolean = true
) {
    val delta: Double get() = current - previous
    val percentChange: Double? get() = if (previous == 0.0) null else (delta / previous) * 100.0
}

private data class MonthTotalsRow(
    val monthLabel: String,
    val income: Double,
    val expense: Double
) {
    val balance: Double get() = income - expense
}

@Composable
fun ReportsScreen(state: TrackerState) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val exportSuccessText = stringResource(R.string.report_export_success)
    val exportFailedText = stringResource(R.string.report_export_failed)
    val exportMonthScopeToast = stringResource(R.string.report_export_month_scope_only)

    val defaultYear = SimpleDateFormat("yyyy", Locale.GERMANY).format(Date()).toIntOrNull() ?: 2026
    val currentMonth = SimpleDateFormat("MM", Locale.GERMANY).format(Date()).toIntOrNull() ?: 1
    var selectedYearText by remember { mutableStateOf(defaultYear.toString()) }
    val selectedYear = selectedYearText.toIntOrNull() ?: defaultYear
    val prevYear = selectedYear - 1

    var exportScope by remember { mutableStateOf(ReportExportScope.SUMMARY) }
    var exportFormat by remember { mutableStateOf(ReportExportFormat.PDF) }
    var exportMonth by remember { mutableStateOf(currentMonth) }
    var pendingExport by remember { mutableStateOf<ExportPayload?>(null) }

    val monthLabels = monthLabels()
    val monthlyRows = monthlyTotalsForYear(state, selectedYear)
    val previousTotals = totalsForYear(state, prevYear)
    val currentTotals = totalsForYear(state, selectedYear)
    val previousBookingCount = bookingCountForYear(state, prevYear)
    val currentBookingCount = bookingCountForYear(state, selectedYear)
    val compareRows = listOf(
        CompareRow(stringResource(R.string.report_income), previousTotals.first, currentTotals.first, isCurrency = true),
        CompareRow(stringResource(R.string.report_expense), previousTotals.second, currentTotals.second, isCurrency = true),
        CompareRow(stringResource(R.string.report_balance), previousTotals.first - previousTotals.second, currentTotals.first - currentTotals.second, isCurrency = true),
        CompareRow(stringResource(R.string.bookings_section_list), previousBookingCount.toDouble(), currentBookingCount.toDouble(), isCurrency = false)
    )

    LaunchedEffect(exportScope) {
        if (exportScope == ReportExportScope.MONTH_BOOKINGS) {
            exportMonth = currentMonth
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        val payload = pendingExport
        if (uri == null || payload == null) {
            pendingExport = null
            return@rememberLauncherForActivityResult
        }

        val written = runCatching {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(payload.bytes)
            }
        }.isSuccess

        pendingExport = null
        Toast.makeText(
            context,
            if (written) exportSuccessText else exportFailedText,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun startExport() {
        val payload = buildExportPayload(
            state = state,
            year = selectedYear,
            month = exportMonth,
            scope = exportScope,
            format = exportFormat
        )
        pendingExport = payload
        createDocumentLauncher.launch(payload.filename)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.report_panel_title), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = selectedYearText,
                        onValueChange = { selectedYearText = it },
                        label = { Text(stringResource(R.string.label_report_year)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ReportSelectField(
                        label = stringResource(R.string.label_export_scope),
                        selectedLabel = when (exportScope) {
                            ReportExportScope.SUMMARY -> stringResource(R.string.export_scope_summary)
                            ReportExportScope.YEAR_COMPARISON -> stringResource(R.string.export_scope_year_comparison)
                            ReportExportScope.YEAR_BOOKINGS -> stringResource(R.string.export_scope_year_bookings)
                            ReportExportScope.MONTH_BOOKINGS -> stringResource(R.string.export_scope_month_bookings)
                        },
                        options = listOf(
                            stringResource(R.string.export_scope_summary) to ReportExportScope.SUMMARY,
                            stringResource(R.string.export_scope_year_comparison) to ReportExportScope.YEAR_COMPARISON,
                            stringResource(R.string.export_scope_year_bookings) to ReportExportScope.YEAR_BOOKINGS,
                            stringResource(R.string.export_scope_month_bookings) to ReportExportScope.MONTH_BOOKINGS
                        ),
                        onSelected = { exportScope = it }
                    )
                    ReportSelectField(
                        label = stringResource(R.string.label_export_month),
                        selectedLabel = monthLabels.getOrNull(exportMonth - 1) ?: exportMonth.toString(),
                        options = (1..12).map { m -> (monthLabels.getOrNull(m - 1) ?: m.toString()) to m },
                        onSelected = { exportMonth = it },
                        enabled = exportScope == ReportExportScope.MONTH_BOOKINGS,
                        onDisabledClick = {
                            Toast.makeText(context, exportMonthScopeToast, Toast.LENGTH_SHORT).show()
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ReportFormatButton(
                            label = stringResource(R.string.label_export_format),
                            selectedLabel = when (exportFormat) {
                                ReportExportFormat.CSV -> "CSV"
                                ReportExportFormat.PDF -> "PDF"
                                ReportExportFormat.XLSX -> "XLSX"
                            },
                            options = listOf(
                                "PDF" to ReportExportFormat.PDF,
                                "XLSX" to ReportExportFormat.XLSX,
                                "CSV" to ReportExportFormat.CSV
                            ),
                            onSelected = { exportFormat = it },
                            modifier = Modifier.width(132.dp)
                        )
                        Button(
                            onClick = { startExport() },
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        ) {
                            Text(stringResource(R.string.button_export))
                        }
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.report_compare_table_title), style = MaterialTheme.typography.titleMedium)
                    TableHeader(
                        listOf(
                            stringResource(R.string.report_col_metric),
                            prevYear.toString(),
                            selectedYear.toString(),
                            stringResource(R.string.report_col_delta)
                        )
                    )
                    compareRows.forEach { row ->
                        CompareTableRow(row = row)
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.report_month_table_title), style = MaterialTheme.typography.titleMedium)
                    TableHeader(
                        listOf(
                            stringResource(R.string.report_col_month),
                            stringResource(R.string.report_income),
                            stringResource(R.string.report_expense),
                            stringResource(R.string.report_balance)
                        )
                    )
                    monthlyRows.forEach { row ->
                        TableRow(
                            listOf(
                                row.monthLabel,
                                formatCurrency(row.income),
                                formatCurrency(row.expense),
                                formatCurrency(row.balance)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> ReportFormatButton(
    label: String,
    selectedLabel: String,
    options: List<Pair<String, T>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = label,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                options.forEach { (text, value) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelected(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ReportSelectField(
    label: String,
    selectedLabel: String,
    options: List<Pair<String, T>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onDisabledClick: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = {
                IconButton(
                    enabled = enabled,
                    onClick = {
                        if (enabled) {
                            expanded = !expanded
                            if (!expanded) focusManager.clearFocus(force = true)
                        } else {
                            onDisabledClick?.invoke()
                        }
                    }
                ) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (enabled) 1f else 0.6f)
                .clickable(enabled = !enabled) { onDisabledClick?.invoke() }
                .clickable(enabled = enabled) { expanded = true }
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && enabled) expanded = true
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus(force = true)
            },
            containerColor = MaterialTheme.colorScheme.surface,
            properties = PopupProperties(focusable = false)
        ) {
            options.forEach { (text, value) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                        focusManager.clearFocus(force = true)
                    }
                )
            }
        }
    }
}

@Composable
private fun TableHeader(columns: List<String>) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        columns.forEachIndexed { index, col ->
            Text(
                text = col,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                modifier = Modifier
                    .weight(if (index == 0) 1.2f else 1f)
            )
        }
    }
}

@Composable
private fun TableRow(columns: List<String>) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        columns.forEachIndexed { index, col ->
            Text(
                text = col,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier
                    .weight(if (index == 0) 1.2f else 1f)
            )
        }
    }
}

@Composable
private fun CompareTableRow(row: CompareRow) {
    val deltaClassColor = when {
        row.delta > 0 -> Color(0xFF047857)
        row.delta < 0 -> Color(0xFFB91C1C)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val deltaSign = if (row.delta > 0) "+" else ""
    val previousText = if (row.isCurrency) formatCurrency(row.previous) else row.previous.toInt().toString()
    val currentText = if (row.isCurrency) formatCurrency(row.current) else row.current.toInt().toString()
    val deltaValueText = if (row.isCurrency) {
        "$deltaSign${formatCurrency(row.delta)}"
    } else {
        "$deltaSign${row.delta.toInt()}"
    }
    val pctText = row.percentChange?.let {
        val pctSign = if (it > 0) "+" else ""
        "$pctSign${String.format(Locale.GERMANY, "%.1f", it)}%"
    } ?: "-"
    val changeText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = deltaClassColor)) {
            append(deltaValueText)
        }
        append(" ($pctText)")
    }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = row.metric,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = previousText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = currentText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = changeText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun totalsForYear(state: TrackerState, year: Int): Pair<Double, Double> {
    val entries = state.bookings.filter { it.userId == state.activeUserId && it.date.takeLast(4) == year.toString() }
    val income = entries.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val expense = entries.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    return income to expense
}

private fun bookingCountForYear(state: TrackerState, year: Int): Int =
    state.bookings.count { it.userId == state.activeUserId && it.date.takeLast(4) == year.toString() }

private fun monthlyTotalsForYear(state: TrackerState, year: Int): List<MonthTotalsRow> {
    val monthLabels = monthLabels()
    val entries = state.bookings.filter { it.userId == state.activeUserId && it.date.takeLast(4) == year.toString() }
    return (1..12).map { month ->
        val monthToken = month.toString().padStart(2, '0')
        val monthEntries = entries.filter {
            val parts = it.date.split(".")
            parts.size >= 3 && parts[1] == monthToken
        }
        MonthTotalsRow(
            monthLabel = monthLabels.getOrNull(month - 1) ?: monthToken,
            income = monthEntries.filter { it.txType == TxType.INCOME }.sumOf { it.amount },
            expense = monthEntries.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
        )
    }
}

private fun buildExportPayload(
    state: TrackerState,
    year: Int,
    month: Int,
    scope: ReportExportScope,
    format: ReportExportFormat
): ExportPayload {
    val csv = buildCsvContent(state, year, month, scope)
    val bytes = when (format) {
        ReportExportFormat.CSV -> csv.toByteArray(Charsets.UTF_8)
        ReportExportFormat.PDF -> buildDesktopLikePdfBytes(state, year, month, scope)
        ReportExportFormat.XLSX -> buildDesktopLikeXlsxBytes(state, year, month, scope)
    }
    val filename = reportExportFilename(year, month, scope, format)
    return ExportPayload(filename = filename, bytes = bytes)
}

private fun reportExportFilename(
    year: Int,
    month: Int,
    scope: ReportExportScope,
    format: ReportExportFormat
): String {
    return when (scope) {
        ReportExportScope.SUMMARY -> "auswertung_${year}.${format.extension}"
        ReportExportScope.YEAR_COMPARISON -> "jahresvergleich_${year}_vs_${year - 1}.${format.extension}"
        ReportExportScope.YEAR_BOOKINGS -> "buchungen_${year}.${format.extension}"
        ReportExportScope.MONTH_BOOKINGS -> "buchungen_${month.toString().padStart(2, '0')}_${year}.${format.extension}"
    }
}

private fun buildCsvContent(
    state: TrackerState,
    year: Int,
    month: Int,
    scope: ReportExportScope
): String {
    val sb = StringBuilder()
    val userName = state.users.firstOrNull { it.id == state.activeUserId }?.name.orEmpty()
    sb.append("ExportScope;${scope.key}\nYear;$year\nUser;$userName\n\n")
    when (scope) {
        ReportExportScope.SUMMARY -> {
            sb.append("Month;Income;Expense;Balance\n")
            monthlyTotalsForYear(state, year).forEach { r -> sb.append("${r.monthLabel};${fmt(r.income)};${fmt(r.expense)};${fmt(r.balance)}\n") }
        }
        ReportExportScope.YEAR_COMPARISON -> {
            val prev = totalsForYear(state, year - 1)
            val curr = totalsForYear(state, year)
            sb.append("Metric;PreviousYear;CurrentYear;Delta\n")
            sb.append("Income;${fmt(prev.first)};${fmt(curr.first)};${fmt(curr.first - prev.first)}\n")
            sb.append("Expense;${fmt(prev.second)};${fmt(curr.second)};${fmt(curr.second - prev.second)}\n")
            sb.append("Balance;${fmt(prev.first - prev.second)};${fmt(curr.first - curr.second)};${fmt((curr.first - curr.second) - (prev.first - prev.second))}\n")
        }
        ReportExportScope.YEAR_BOOKINGS -> {
            sb.append("Date;Description;Category;Type;Amount;Account;Note;Tax\n")
            bookingsForYear(state, year).forEach { sb.append(bookingCsvLine(it)).append('\n') }
        }
        ReportExportScope.MONTH_BOOKINGS -> {
            sb.append("Date;Description;Category;Type;Amount;Account;Note;Tax\n")
            bookingsForMonth(state, year, month).forEach { sb.append(bookingCsvLine(it)).append('\n') }
        }
    }
    return sb.toString()
}

private fun buildDesktopLikeXlsxBytes(
    state: TrackerState,
    year: Int,
    month: Int,
    scope: ReportExportScope
): ByteArray {
    val sheet = when (scope) {
        ReportExportScope.SUMMARY -> {
            val totals = totalsForYear(state, year)
            val rows = mutableListOf<List<String>>(
                listOf("Export", "Finanz Tracker Jahresauswertung"),
                listOf("Jahr", year.toString()),
                listOf("Währung", "EUR"),
                emptyList(),
                listOf("Monat", "Einnahmen", "Ausgaben", "Saldo")
            )
            monthlyTotalsForYear(state, year).forEach { row ->
                rows.add(listOf(row.monthLabel, fmt(row.income), fmt(row.expense), fmt(row.balance)))
            }
            rows.add(emptyList())
            rows.add(listOf("Gesamt", fmt(totals.first), fmt(totals.second), fmt(totals.first - totals.second)))
            XlsxSheet(name = "Auswertung $year", rows = rows)
        }

        ReportExportScope.YEAR_COMPARISON -> {
            val prev = totalsForYear(state, year - 1)
            val curr = totalsForYear(state, year)
            val rows = listOf(
                listOf("Export", "Finanz Tracker Jahresvergleich"),
                listOf("Jahr", year.toString()),
                listOf("Vorjahr", (year - 1).toString()),
                listOf("Währung", "EUR"),
                emptyList(),
                listOf("Kennzahl", (year - 1).toString(), year.toString(), "Veränderung"),
                listOf("Einnahmen", fmt(prev.first), fmt(curr.first), fmt(curr.first - prev.first)),
                listOf("Ausgaben", fmt(prev.second), fmt(curr.second), fmt(curr.second - prev.second)),
                listOf(
                    "Saldo",
                    fmt(prev.first - prev.second),
                    fmt(curr.first - curr.second),
                    fmt((curr.first - curr.second) - (prev.first - prev.second))
                )
            )
            XlsxSheet(name = "Vergleich $year", rows = rows)
        }

        ReportExportScope.YEAR_BOOKINGS -> {
            val rows = mutableListOf<List<String>>(
                listOf("Export", "Finanz Tracker Buchungsliste"),
                listOf("Zeitraum", year.toString()),
                listOf("Währung", "EUR"),
                emptyList(),
                listOf("Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz")
            )
            bookingsForYear(state, year).forEach { b -> rows.add(bookingXlsxRow(b)) }
            XlsxSheet(name = "Buchungen $year", rows = rows)
        }

        ReportExportScope.MONTH_BOOKINGS -> {
            val rows = mutableListOf<List<String>>(
                listOf("Export", "Finanz Tracker Buchungsliste"),
                listOf("Zeitraum", "${month.toString().padStart(2, '0')}.$year"),
                listOf("Währung", "EUR"),
                emptyList(),
                listOf("Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz")
            )
            bookingsForMonth(state, year, month).forEach { b -> rows.add(bookingXlsxRow(b)) }
            XlsxSheet(name = "Buchungen ${month.toString().padStart(2, '0')}-$year", rows = rows)
        }
    }

    return XlsxBuilder.buildWorkbook(listOf(sheet))
}

private fun bookingCsvLine(booking: Booking): String {
    val type = if (booking.txType == TxType.INCOME) "Einnahme" else "Ausgabe"
    val tax = if (booking.taxDeclaration) "Ja" else "Nein"
    return listOf(
        booking.date,
        sanitizeCsvField(booking.description),
        sanitizeCsvField(booking.category),
        type,
        fmt(booking.amount),
        sanitizeCsvField(booking.account),
        sanitizeCsvField(booking.note),
        tax
    ).joinToString(";")
}

private fun bookingXlsxRow(booking: Booking): List<String> {
    val type = if (booking.txType == TxType.INCOME) "Einnahme" else "Ausgabe"
    val tax = if (booking.taxDeclaration) "Ja" else "Nein"
    return listOf(
        booking.date,
        booking.description,
        booking.category,
        type,
        fmt(booking.amount),
        booking.account,
        tax,
        booking.note
    )
}

private fun sanitizeCsvField(value: String): String = value.replace("\n", " ").replace(";", ",").trim()

private fun bookingsForYear(state: TrackerState, year: Int): List<Booking> =
    state.bookings.filter { it.userId == state.activeUserId && it.date.takeLast(4) == year.toString() }.sortedByDescending { it.createdAt }

private fun bookingsForMonth(state: TrackerState, year: Int, month: Int): List<Booking> {
    val monthToken = month.toString().padStart(2, '0')
    return bookingsForYear(state, year).filter {
        val parts = it.date.split(".")
        parts.size >= 3 && parts[1] == monthToken
    }
}

private fun buildDesktopLikePdfBytes(
    state: TrackerState,
    year: Int,
    month: Int,
    scope: ReportExportScope
): ByteArray {
    return when (scope) {
        ReportExportScope.SUMMARY -> buildPdfSummaryBytes(state, year)
        ReportExportScope.YEAR_COMPARISON -> buildPdfComparisonBytes(state, year)
        ReportExportScope.YEAR_BOOKINGS -> buildPdfBookingsBytes(state, year, month = null)
        ReportExportScope.MONTH_BOOKINGS -> buildPdfBookingsBytes(state, year, month = month)
    }
}

private fun buildPdfSummaryBytes(state: TrackerState, year: Int): ByteArray {
    val monthly = monthlyTotalsForYear(state, year)
    val totals = totalsForYear(state, year)
    val createdAt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date())

    val document = PdfDocument()
    val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    val canvas = page.canvas

    val titlePaint = Paint().apply { textSize = 14f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    val metaPaint = Paint().apply { textSize = 10f }

    canvas.drawText("Finanz Tracker Jahresauswertung", 40f, 44f, titlePaint)
    canvas.drawText("Jahr: $year", 40f, 66f, metaPaint)
    canvas.drawText("Erstellt am: $createdAt", 40f, 82f, metaPaint)
    canvas.drawText("Währung: EUR", 40f, 98f, metaPaint)

    val head = listOf("Monat", "Einnahmen", "Ausgaben", "Saldo")
    val body = monthly.map { row ->
        listOf(row.monthLabel, formatCurrency(row.income), formatCurrency(row.expense), formatCurrency(row.balance))
    }
    val finalY = drawPdfTable(
        canvas = canvas,
        startX = 40f,
        startY = 116f,
        columnWidths = floatArrayOf(140f, 120f, 120f, 120f),
        head = head,
        body = body
    )

    val totalsPaint = Paint().apply { textSize = 10f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    canvas.drawText(
        "Gesamtsumme  Einnahmen: ${formatCurrency(totals.first)}   Ausgaben: ${formatCurrency(totals.second)}   Saldo: ${formatCurrency(totals.first - totals.second)}",
        40f,
        finalY + 24f,
        totalsPaint
    )

    document.finishPage(page)
    return writePdfDocument(document)
}

private fun buildPdfComparisonBytes(state: TrackerState, year: Int): ByteArray {
    val prevYear = year - 1
    val previous = totalsForYear(state, prevYear)
    val current = totalsForYear(state, year)
    val createdAt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date())

    val rows = listOf(
        listOf("Einnahmen", formatCurrency(previous.first), formatCurrency(current.first), formatCurrency(current.first - previous.first), "-"),
        listOf("Ausgaben", formatCurrency(previous.second), formatCurrency(current.second), formatCurrency(current.second - previous.second), "-"),
        listOf(
            "Saldo",
            formatCurrency(previous.first - previous.second),
            formatCurrency(current.first - current.second),
            formatCurrency((current.first - current.second) - (previous.first - previous.second)),
            "-"
        )
    )

    val document = PdfDocument()
    val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    val canvas = page.canvas

    val titlePaint = Paint().apply { textSize = 14f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    val metaPaint = Paint().apply { textSize = 10f }

    canvas.drawText("Finanz Tracker Jahresvergleich", 40f, 44f, titlePaint)
    canvas.drawText("Jahr: $year", 40f, 66f, metaPaint)
    canvas.drawText("Vorjahr: $prevYear", 40f, 82f, metaPaint)
    canvas.drawText("Erstellt am: $createdAt", 40f, 98f, metaPaint)
    canvas.drawText("Währung: EUR", 40f, 114f, metaPaint)

    drawPdfTable(
        canvas = canvas,
        startX = 40f,
        startY = 132f,
        columnWidths = floatArrayOf(130f, 100f, 100f, 100f, 100f),
        head = listOf("Kennzahl", prevYear.toString(), year.toString(), "Veränderung", "Veränderung %"),
        body = rows
    )

    document.finishPage(page)
    return writePdfDocument(document)
}

private fun buildPdfBookingsBytes(state: TrackerState, year: Int, month: Int?): ByteArray {
    val rowsSource = if (month == null) bookingsForYear(state, year) else bookingsForMonth(state, year, month)
    val createdAt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date())
    val scopeLabel = if (month == null) {
        year.toString()
    } else {
        "${month.toString().padStart(2, '0')}.${year}"
    }

    val income = rowsSource.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val expense = rowsSource.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val net = income - expense

    val body = rowsSource.map { b ->
        listOf(
            b.date,
            b.description,
            b.category,
            if (b.txType == TxType.INCOME) "Einnahme" else "Ausgabe",
            formatCurrency(b.amount),
            b.account,
            if (b.taxDeclaration) "Ja" else "Nein",
            b.note
        )
    }

    val document = PdfDocument()
    val page = document.startPage(PdfDocument.PageInfo.Builder(842, 595, 1).create())
    val canvas = page.canvas

    val titlePaint = Paint().apply { textSize = 14f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    val metaPaint = Paint().apply { textSize = 10f }

    canvas.drawText("Finanz Tracker Buchungsliste", 40f, 44f, titlePaint)
    canvas.drawText("Zeitraum: $scopeLabel", 40f, 66f, metaPaint)
    canvas.drawText("Erstellt am: $createdAt", 40f, 82f, metaPaint)
    canvas.drawText("Währung: EUR", 40f, 98f, metaPaint)

    val finalY = drawPdfTable(
        canvas = canvas,
        startX = 40f,
        startY = 116f,
        columnWidths = floatArrayOf(64f, 160f, 112f, 58f, 68f, 90f, 78f, 118f),
        head = listOf("Datum", "Beschreibung", "Kategorie", "Typ", "Betrag", "Konto", "Steuererklärung", "Notiz"),
        body = body
    )

    val totalsPaint = Paint().apply { textSize = 10f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    canvas.drawText(
        "Buchungen: ${rowsSource.size}   Einnahmen: ${formatCurrency(income)}   Ausgaben: ${formatCurrency(expense)}   Saldo: ${formatCurrency(net)}",
        40f,
        (finalY + 22f).coerceAtMost(575f),
        totalsPaint
    )

    document.finishPage(page)
    return writePdfDocument(document)
}

private fun drawPdfTable(
    canvas: android.graphics.Canvas,
    startX: Float,
    startY: Float,
    columnWidths: FloatArray,
    head: List<String>,
    body: List<List<String>>
): Float {
    var y = startY
    val headerHeight = 18f
    val rowHeight = 16f
    val borderPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 1f }
    val headerFillPaint = Paint().apply { style = Paint.Style.FILL; color = 0xFF0F766E.toInt() }
    val headerTextPaint = Paint().apply { textSize = 9f; color = 0xFFFFFFFF.toInt(); typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
    val bodyTextPaint = Paint().apply { textSize = 8.5f; color = 0xFF111111.toInt() }

    var x = startX
    head.forEachIndexed { index, label ->
        val width = columnWidths.getOrElse(index) { 100f }
        canvas.drawRect(x, y, x + width, y + headerHeight, headerFillPaint)
        canvas.drawRect(x, y, x + width, y + headerHeight, borderPaint)
        canvas.drawText(label.take(24), x + 4f, y + 12.5f, headerTextPaint)
        x += width
    }
    y += headerHeight

    body.forEach { row ->
        x = startX
        row.forEachIndexed { index, value ->
            val width = columnWidths.getOrElse(index) { 100f }
            canvas.drawRect(x, y, x + width, y + rowHeight, borderPaint)
            canvas.drawText(value.take(30), x + 4f, y + 11.5f, bodyTextPaint)
            x += width
        }
        y += rowHeight
    }
    return y
}

private fun writePdfDocument(document: PdfDocument): ByteArray {
    val out = ByteArrayOutputStream()
    document.writeTo(out)
    document.close()
    return out.toByteArray()
}

private fun monthLabels(): List<String> =
    DateFormatSymbols.getInstance(Locale.getDefault()).months.take(12).mapIndexed { index, m ->
        if (m.isBlank()) (index + 1).toString().padStart(2, '0') else m
    }

private fun fmt(value: Double): String = String.format(Locale.US, "%.2f", value)
private fun formatCurrency(value: Double): String = String.format(Locale.GERMANY, "%.2f €", value)
