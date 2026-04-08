package com.darexsh.finanztracker.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
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
    val maxValue = monthTrend.maxOfOrNull { maxOf(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0

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
                    monthTrend.forEach { row ->
                        MonthTrendBar(
                            row = row,
                            maxValue = maxValue
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
            onDismissRequest = { expanded = false }
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
            onDismissRequest = { expanded = false }
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
private fun MonthTrendBar(row: MonthTrendRow, maxValue: Double) {
    val incomeRatio = (row.income / maxValue).toFloat().coerceIn(0f, 1f)
    val expenseRatio = (row.expense / maxValue).toFloat().coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(row.month, style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width((220f * incomeRatio).dp)
                    .background(Color(0xFF1AA251))
            )
            Text(stringResource(R.string.format_eur, row.income), style = MaterialTheme.typography.bodySmall)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width((220f * expenseRatio).dp)
                    .background(Color(0xFFD7263D))
            )
            Text(stringResource(R.string.format_eur, row.expense), style = MaterialTheme.typography.bodySmall)
        }
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
