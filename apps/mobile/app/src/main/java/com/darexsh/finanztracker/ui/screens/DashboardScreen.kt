package com.darexsh.finanztracker.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.FinanceCatalog
import com.darexsh.finanztracker.model.CurrencyPreference
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import com.darexsh.finanztracker.ui.components.SelectionBottomSheetButton
import com.darexsh.finanztracker.ui.FinanceLabelLocalizer
import com.darexsh.finanztracker.ui.formatCurrencyValue
import kotlin.math.max
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TopCategoryRow(val category: String, val amount: Double)
private data class MonthTrendRow(val month: String, val income: Double, val expense: Double)

@Composable
fun DashboardScreen(
    state: TrackerState,
    currency: CurrencyPreference = CurrencyPreference.EUR
) {
    val activeBookings = state.bookings.filter { it.userId == state.activeUserId }
    val accountOptions = remember(activeBookings) {
        (FinanceCatalog.accounts + activeBookings.map { it.account })
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase(Locale.ROOT) }
    }
    var selectedAccount by remember(state.activeUserId) { mutableStateOf<String?>(null) }
    if (selectedAccount != null && accountOptions.none { it.equals(selectedAccount, ignoreCase = true) }) {
        selectedAccount = null
    }

    val dashboardBookings = if (selectedAccount == null) {
        activeBookings
    } else {
        activeBookings.filter { it.account.equals(selectedAccount, ignoreCase = true) }
    }
    val selectedAccountLabel = selectedAccount?.let { FinanceLabelLocalizer.localizeAccount(it, Locale.getDefault()) }
        ?: stringResource(R.string.filter_all_accounts)

    val totalIncome = dashboardBookings.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val totalExpense = dashboardBookings.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val now = Date()
    val currentMonthToken = SimpleDateFormat("MM", Locale.GERMANY).format(now)
    val currentYearToken = SimpleDateFormat("yyyy", Locale.GERMANY).format(now)
    val currentYearInt = currentYearToken.toIntOrNull() ?: 2026
    var selectedTopMonth by remember { mutableStateOf(currentMonthToken.toIntOrNull() ?: 1) }
    var selectedTrendYear by remember { mutableStateOf(currentYearInt) }
    val selectedTopMonthToken = selectedTopMonth.toString().padStart(2, '0')
    val monthBookings = dashboardBookings.filter {
        val parts = parseDashboardDateParts(it.date) ?: return@filter false
        parts.month == selectedTopMonth && parts.year == selectedTrendYear
    }
    val monthlyIncome = monthBookings.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val monthlyExpense = monthBookings.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val monthlySurplus = monthlyIncome - monthlyExpense

    val monthLabels = DateFormatSymbols.getInstance(Locale.getDefault()).months.take(12)
    val topCategories = dashboardBookings
        .filter {
            val parts = parseDashboardDateParts(it.date) ?: return@filter false
            parts.month == selectedTopMonth && parts.year == selectedTrendYear
        }
        .filter { it.txType == TxType.EXPENSE }
        .groupBy { it.category.ifBlank { stringResource(R.string.default_category) } }
        .map { (category, rows) -> TopCategoryRow(category, rows.sumOf { it.amount }) }
        .sortedByDescending { it.amount }
        .take(5)
    val selectedTopMonthLabel = monthLabels.getOrNull(selectedTopMonth - 1)
        ?.takeIf { it.isNotBlank() }
        ?: selectedTopMonth.toString().padStart(2, '0')

    val trendYearOptions = remember(dashboardBookings, currentYearInt) {
        val years = dashboardBookings.mapNotNull { booking ->
            parseDashboardDateParts(booking.date)?.year
        }.toMutableSet()
        years.add(currentYearInt)
        years.toList().sortedDescending()
    }
    if (!trendYearOptions.contains(selectedTrendYear)) {
        selectedTrendYear = trendYearOptions.firstOrNull() ?: currentYearInt
    }
    val monthTrend = (1..12).map { month ->
        val rows = dashboardBookings.filter {
            val parts = parseDashboardDateParts(it.date) ?: return@filter false
            parts.month == month && parts.year == selectedTrendYear
        }
        MonthTrendRow(
            month = monthLabels.getOrNull(month - 1).takeUnless { it.isNullOrBlank() } ?: month.toString().padStart(2, '0'),
            income = rows.filter { it.txType == TxType.INCOME }.sumOf { it.amount },
            expense = rows.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.screen_overview), style = MaterialTheme.typography.headlineSmall)
                SelectionBottomSheetButton(
                    title = stringResource(R.string.filter_account),
                    selectedLabel = "${stringResource(R.string.label_account)}: $selectedAccountLabel",
                    options = listOf(stringResource(R.string.filter_all_accounts) to null) +
                        accountOptions.map { FinanceLabelLocalizer.localizeAccount(it, Locale.getDefault()) to it },
                    onSelected = { selectedAccount = it },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    isSelected = { it == selectedAccount }
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = stringResource(R.string.summary_current_balance),
                        value = formatCurrencyValue(balance, currency),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = stringResource(R.string.summary_income),
                        value = formatCurrencyValue(monthlyIncome, currency),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = stringResource(R.string.summary_expense),
                        value = formatCurrencyValue(monthlyExpense, currency),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = stringResource(R.string.summary_monthly_surplus),
                        value = formatCurrencyValue(monthlySurplus, currency),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.dashboard_top_categories_title), style = MaterialTheme.typography.titleMedium)
                        SelectionBottomSheetButton(
                            title = stringResource(R.string.dashboard_top_categories_month_label),
                            selectedLabel = monthLabels.getOrNull(selectedTopMonth - 1)
                                ?.takeIf { it.isNotBlank() }
                                ?: selectedTopMonth.toString().padStart(2, '0'),
                            options = (1..12).map { month ->
                                ((monthLabels.getOrNull(month - 1)?.takeIf { it.isNotBlank() }
                                    ?: month.toString().padStart(2, '0')) to month)
                            },
                            onSelected = { selectedTopMonth = it },
                            modifier = Modifier.width(94.dp),
                            isSelected = { it == selectedTopMonth }
                        )
                    }
                    if (topCategories.isEmpty()) {
                        Text(
                            stringResource(
                                R.string.dashboard_top_categories_empty_for_month,
                                selectedTopMonthLabel,
                                selectedTrendYear.toString()
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        topCategories.forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    FinanceLabelLocalizer.localizeCategory(row.category, Locale.getDefault()),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(formatCurrencyValue(row.amount, currency), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.dashboard_month_trend_title, selectedTrendYear.toString()), style = MaterialTheme.typography.titleMedium)
                        SelectionBottomSheetButton(
                            title = stringResource(R.string.label_report_year),
                            selectedLabel = selectedTrendYear.toString(),
                            options = trendYearOptions.map { it.toString() to it },
                            onSelected = { selectedTrendYear = it },
                            modifier = Modifier.width(88.dp),
                            isSelected = { it == selectedTrendYear }
                        )
                    }
                    Text(
                        stringResource(R.string.dashboard_trend_legend),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(8.dp)
                    ) {
                        MonthlyCashflowChart(
                            rows = monthTrend,
                            selectedYear = selectedTrendYear,
                            currency = currency,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

}

private data class DashboardDateParts(val day: Int, val month: Int, val year: Int)

private fun parseDashboardDateParts(raw: String): DashboardDateParts? {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return null
    val parts = trimmed.split('.', '/', '-').map { it.trim() }
    if (parts.size != 3) return null

    val a = parts[0].toIntOrNull() ?: return null
    val b = parts[1].toIntOrNull() ?: return null
    val c = parts[2].toIntOrNull() ?: return null

    val parsed = if (parts[0].length == 4) {
        DashboardDateParts(day = c, month = b, year = a) // yyyy-MM-dd
    } else {
        DashboardDateParts(day = a, month = b, year = c) // dd.MM.yyyy or dd/MM/yyyy
    }

    if (parsed.month !in 1..12) return null
    if (parsed.day !in 1..31) return null
    if (parsed.year !in 1900..2100) return null
    return parsed
}


@Composable
private fun MonthlyCashflowChart(
    rows: List<MonthTrendRow>,
    selectedYear: Int,
    currency: CurrencyPreference,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var chartSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(rows, selectedYear) {
        selectedIndex = null
    }
    val maxScale = rows.maxOfOrNull { max(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0
    val density = LocalDensity.current
    val axisTextPx = with(density) { 12.sp.toPx() }
    val monthTextPx = with(density) { 12.sp.toPx() }
    val yearTextPx = with(density) { 12.sp.toPx() }
    val axisPaint = remember {
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#64748B")
            textSize = axisTextPx
            textAlign = android.graphics.Paint.Align.RIGHT
        }
    }
    val monthPaint = remember {
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#64748B")
            textSize = monthTextPx
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val yearPaint = remember {
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#64748B")
            textSize = yearTextPx
            textAlign = android.graphics.Paint.Align.RIGHT
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
    }
    val padLeft = with(density) { 44.dp.toPx() }
    val padRight = with(density) { 12.dp.toPx() }
    val padTop = with(density) { 20.dp.toPx() }
    val padBottom = with(density) { 30.dp.toPx() }

    Box(
        modifier = modifier
            .onSizeChanged { chartSize = it }
            .pointerInput(rows, selectedYear, chartSize) {
            detectTapGestures { tap ->
                val width = size.width.toFloat()
                val height = size.height.toFloat()
                val baseY = height - padBottom
                val inChartX = tap.x >= padLeft && tap.x <= (width - padRight)
                val inChartY = tap.y >= padTop && tap.y <= baseY
                if (!inChartX || !inChartY) {
                    selectedIndex = null
                    return@detectTapGestures
                }
                val chartW = width - padLeft - padRight
                if (chartW <= 0f) {
                    selectedIndex = null
                    return@detectTapGestures
                }
                val slot = chartW / 12f
                val raw = ((tap.x - padLeft) / slot).toInt()
                val index = raw.coerceIn(0, 11)
                selectedIndex = if (selectedIndex == index) null else index
            }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val baseY = height - padBottom
            val chartW = width - padLeft - padRight
            val chartH = height - padTop - padBottom
            val maxBarH = chartH - with(density) { 8.dp.toPx() }
            val minBarPx = with(density) { 2.dp.toPx() }

            drawLine(
                color = Color(0xFFDbe4EE),
                start = Offset(padLeft, padTop),
                end = Offset(padLeft, baseY),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0xFFDbe4EE),
                start = Offset(padLeft, baseY),
                end = Offset(width - padRight, baseY),
                strokeWidth = 1.dp.toPx()
            )

            val ticks = listOf(1f, 0.75f, 0.5f, 0.25f, 0f)
            ticks.forEach { tick ->
                val y = baseY - maxBarH * tick
                drawLine(
                    color = if (tick == 0f) Color(0xFFDbe4EE) else Color(0xFFEef2F7),
                    start = Offset(padLeft, y),
                    end = Offset(width - padRight, y),
                    strokeWidth = 1.dp.toPx()
                )
                val rawValue = maxScale * tick
                val label = if (tick == 0f) "0" else compactAxisValue(rawValue)
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        label,
                        padLeft - with(density) { 8.dp.toPx() },
                        y + with(density) { 4.dp.toPx() },
                        axisPaint
                    )
                }
            }

            val slot = chartW / 12f
            val barW = slot * 0.68f

            rows.forEachIndexed { index, row ->
                val x = padLeft + index * slot + (slot - barW) / 2f
                val incomeH = if (row.income > 0.0) {
                    max(minBarPx, ((row.income / maxScale) * maxBarH).toFloat())
                } else {
                    0f
                }
                val expenseRaw = if (row.expense > 0.0) {
                    max(minBarPx, ((row.expense / maxScale) * maxBarH).toFloat())
                } else {
                    0f
                }

                if (incomeH > 0f) {
                    drawRect(
                        color = Color(0xFF22C55E),
                        topLeft = Offset(x, baseY - incomeH),
                        size = androidx.compose.ui.geometry.Size(barW, incomeH)
                    )
                }
                if (expenseRaw > 0f) {
                    drawRect(
                        color = Color(0xFFEF4444),
                        topLeft = Offset(x, baseY - expenseRaw),
                        size = androidx.compose.ui.geometry.Size(barW, expenseRaw)
                    )
                }

                if (selectedIndex == index) {
                    val visibleH = max(incomeH, expenseRaw).coerceAtLeast(minBarPx)
                    val top = baseY - visibleH
                    drawRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(x - 1.5f, top - 1.5f),
                        size = androidx.compose.ui.geometry.Size(barW + 3f, visibleH + 3f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                    )
                }

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        (index + 1).toString(),
                        x + barW / 2f,
                        height - with(density) { 10.dp.toPx() },
                        monthPaint
                    )
                }
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    selectedYear.toString(),
                    width - padRight,
                    with(density) { 12.dp.toPx() },
                    yearPaint
                )
            }
        }

        val row = selectedIndex?.let { rows.getOrNull(it) }
        if (row != null && chartSize.width > 0 && chartSize.height > 0) {
            val saldo = row.income - row.expense
            val chartWidthPx = chartSize.width.toFloat()
            val chartHeightPx = chartSize.height.toFloat()
            val baseY = chartHeightPx - padBottom
            val chartW = chartWidthPx - padLeft - padRight
            val chartH = chartHeightPx - padTop - padBottom
            val maxBarH = chartH - with(density) { 8.dp.toPx() }
            val minBarPx = with(density) { 2.dp.toPx() }
            val slot = chartW / 12f
            val barW = slot * 0.68f
            val index = selectedIndex ?: 0
            val x = padLeft + index * slot + (slot - barW) / 2f
            val incomeH = if (row.income > 0.0) {
                max(minBarPx, ((row.income / maxScale) * maxBarH).toFloat())
            } else {
                0f
            }
            val expenseRaw = if (row.expense > 0.0) {
                max(minBarPx, ((row.expense / maxScale) * maxBarH).toFloat())
            } else {
                0f
            }
            val visibleH = max(incomeH, expenseRaw).coerceAtLeast(minBarPx)
            val barTop = baseY - visibleH

            val popupWidthPx = with(density) { 214.dp.toPx() }
            val popupHeightPx = with(density) { 108.dp.toPx() }
            val marginPx = with(density) { 8.dp.toPx() }

            val targetCenterX = x + barW / 2f
            val unclampedX = targetCenterX - popupWidthPx / 2f
            val popupX = unclampedX.coerceIn(
                marginPx,
                chartWidthPx - popupWidthPx - marginPx
            )
            val aboveY = barTop - popupHeightPx - marginPx
            val belowY = barTop + marginPx
            val popupY = if (aboveY >= marginPx) aboveY else belowY.coerceAtMost(
                chartHeightPx - popupHeightPx - marginPx
            )

            DropdownMenu(
                expanded = true,
                onDismissRequest = { selectedIndex = null },
                offset = DpOffset(
                    x = with(density) { popupX.toDp() },
                    y = with(density) { popupY.toDp() }
                ),
                containerColor = MaterialTheme.colorScheme.surface,
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${row.month} $selectedYear",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${stringResource(R.string.report_income)}: ${formatCurrencyValue(row.income, currency)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF0F766E)
                    )
                    Text(
                        text = "${stringResource(R.string.report_expense)}: ${formatCurrencyValue(row.expense, currency)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDC2626)
                    )
                    Text(
                        text = "${stringResource(R.string.report_balance)}: ${formatCurrencyValue(saldo, currency)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }
        }
    }
}
private fun compactAxisValue(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.GERMANY, "%.1fM", value / 1_000_000)
        value >= 1_000 -> String.format(Locale.GERMANY, "%.1fk", value / 1_000)
        else -> value.toInt().toString()
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
