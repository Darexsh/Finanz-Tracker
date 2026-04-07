package com.darexsh.finanztracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(state: TrackerState) {
    val currentYear = SimpleDateFormat("yyyy", Locale.GERMANY).format(Date()).toIntOrNull() ?: 2026
    val prevYear = currentYear - 1

    val current = totalsForYear(state, currentYear)
    val previous = totalsForYear(state, prevYear)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.screen_reports), style = MaterialTheme.typography.headlineSmall)
        CompareCard(stringResource(R.string.report_income), previous.first, current.first)
        CompareCard(stringResource(R.string.report_expense), previous.second, current.second)
        CompareCard(stringResource(R.string.report_balance), previous.first - previous.second, current.first - current.second)
    }
}

private fun totalsForYear(state: TrackerState, year: Int): Pair<Double, Double> {
    val entries = state.bookings.filter {
        it.userId == state.activeUserId && it.date.takeLast(4) == year.toString()
    }
    val income = entries.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val expense = entries.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    return income to expense
}

@Composable
private fun CompareCard(label: String, previous: Double, current: Double) {
    val delta = current - previous
    Card {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label)
            Text(stringResource(R.string.format_prev_year, previous))
            Text(stringResource(R.string.format_current_year, current))
            Text(stringResource(R.string.format_delta, delta))
        }
    }
}
