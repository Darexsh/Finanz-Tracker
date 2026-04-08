package com.darexsh.finanztracker.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import kotlin.math.max
import kotlin.math.min
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TopCategoryRow(val category: String, val amount: Double)
private data class MonthTrendRow(val month: String, val income: Double, val expense: Double)

@Composable
fun DashboardScreen(state: TrackerState) {
    val activeBookings = state.bookings.filter { it.userId == state.activeUserId }
    val totalIncome = activeBookings.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val totalExpense = activeBookings.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val now = Date()
    val currentMonthToken = SimpleDateFormat("MM", Locale.GERMANY).format(now)
    val currentYearToken = SimpleDateFormat("yyyy", Locale.GERMANY).format(now)
    val currentYearInt = currentYearToken.toIntOrNull() ?: 2026
    var selectedTopMonth by remember { mutableStateOf(currentMonthToken.toIntOrNull() ?: 1) }
    var selectedTrendYear by remember { mutableStateOf(currentYearInt) }
    val monthBookings = activeBookings.filter {
        val parts = it.date.split(".")
        parts.size >= 3 && parts[1] == currentMonthToken && parts[2] == currentYearToken
    }
    val monthlyIncome = monthBookings.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val monthlyExpense = monthBookings.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val monthlySurplus = monthlyIncome - monthlyExpense

    val monthLabels = DateFormatSymbols.getInstance(Locale.getDefault()).months.take(12)
    val topCategories = activeBookings
        .filter {
            val parts = it.date.split(".")
            parts.size >= 3 &&
                parts[1] == selectedTopMonth.toString().padStart(2, '0') &&
                parts[2] == currentYearToken
        }
        .filter { it.txType == TxType.EXPENSE }
        .groupBy { it.category.ifBlank { stringResource(R.string.default_category) } }
        .map { (category, rows) -> TopCategoryRow(category, rows.sumOf { it.amount }) }
        .sortedByDescending { it.amount }
        .take(5)
    val selectedTopMonthLabel = monthLabels.getOrNull(selectedTopMonth - 1)
        ?.takeIf { it.isNotBlank() }
        ?: selectedTopMonth.toString().padStart(2, '0')

    val trendYearOptions = remember(activeBookings, currentYearInt) {
        val years = activeBookings.mapNotNull { booking ->
            booking.date.split(".").getOrNull(2)?.toIntOrNull()
        }.toMutableSet()
        years.add(currentYearInt)
        years.toList().sortedDescending()
    }
    if (!trendYearOptions.contains(selectedTrendYear)) {
        selectedTrendYear = trendYearOptions.firstOrNull() ?: currentYearInt
    }
    val monthTrend = (1..12).map { month ->
        val token = month.toString().padStart(2, '0')
        val rows = activeBookings.filter {
            val parts = it.date.split(".")
            parts.size >= 3 && parts[1] == token && parts[2] == selectedTrendYear.toString()
        }
        MonthTrendRow(
            month = monthLabels.getOrNull(month - 1).takeUnless { it.isNullOrBlank() } ?: token,
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
            Text(stringResource(R.string.screen_overview), style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = stringResource(R.string.summary_current_balance),
                        value = stringResource(R.string.format_eur, balance),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = stringResource(R.string.summary_income),
                        value = stringResource(R.string.format_eur, monthlyIncome),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = stringResource(R.string.summary_expense),
                        value = stringResource(R.string.format_eur, monthlyExpense),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = stringResource(R.string.summary_monthly_surplus),
                        value = stringResource(R.string.format_eur, monthlySurplus),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.dashboard_top_categories_title), style = MaterialTheme.typography.titleMedium)
                        DashboardMonthSelect(
                            selectedMonth = selectedTopMonth,
                            monthLabels = monthLabels,
                            onSelected = { selectedTopMonth = it }
                        )
                    }
                    if (topCategories.isEmpty()) {
                        Text(
                            stringResource(
                                R.string.dashboard_top_categories_empty_for_month,
                                selectedTopMonthLabel,
                                currentYearToken
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        topCategories.forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(row.category, style = MaterialTheme.typography.bodyMedium)
                                Text(stringResource(R.string.format_eur, row.amount), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.dashboard_month_trend_title, selectedTrendYear.toString()), style = MaterialTheme.typography.titleMedium)
                        DashboardYearSelect(
                            selectedYear = selectedTrendYear,
                            yearOptions = trendYearOptions,
                            onSelected = { selectedTrendYear = it }
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardYearSelect(
    selectedYear: Int,
    yearOptions: List<Int>,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .width(88.dp)
                .height(34.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Text(
                text = selectedYear.toString(),
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = stringResource(R.string.label_report_year)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            yearOptions.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DashboardMonthSelect(
    selectedMonth: Int,
    monthLabels: List<String>,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = monthLabels.getOrNull(selectedMonth - 1)
        ?.takeIf { it.isNotBlank() }
        ?: selectedMonth.toString().padStart(2, '0')

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .width(94.dp)
                .height(34.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Text(
                text = selectedLabel,
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = stringResource(R.string.dashboard_top_categories_month_label)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            (1..12).forEach { month ->
                val label = monthLabels.getOrNull(month - 1)
                    ?.takeIf { it.isNotBlank() }
                    ?: month.toString().padStart(2, '0')
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthlyCashflowChart(
    rows: List<MonthTrendRow>,
    selectedYear: Int,
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
                val expenseOverlayH = if (incomeH > 0f) min(expenseRaw, incomeH) else expenseRaw

                if (incomeH > 0f) {
                    drawRect(
                        color = Color(0xFF22C55E),
                        topLeft = Offset(x, baseY - incomeH),
                        size = androidx.compose.ui.geometry.Size(barW, incomeH)
                    )
                }
                if (expenseOverlayH > 0f) {
                    drawRect(
                        color = Color(0xFFEF4444),
                        topLeft = Offset(x, baseY - expenseOverlayH),
                        size = androidx.compose.ui.geometry.Size(barW, expenseOverlayH)
                    )
                }

                if (selectedIndex == index) {
                    val visibleH = max(incomeH, expenseOverlayH).coerceAtLeast(minBarPx)
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
            val expenseOverlayH = if (incomeH > 0f) min(expenseRaw, incomeH) else expenseRaw
            val visibleH = max(incomeH, expenseOverlayH).coerceAtLeast(minBarPx)
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
                        text = "${stringResource(R.string.report_income)}: ${stringResource(R.string.format_eur, row.income)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF0F766E)
                    )
                    Text(
                        text = "${stringResource(R.string.report_expense)}: ${stringResource(R.string.format_eur, row.expense)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDC2626)
                    )
                    Text(
                        text = "${stringResource(R.string.report_balance)}: ${stringResource(R.string.format_eur, saldo)}",
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
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
