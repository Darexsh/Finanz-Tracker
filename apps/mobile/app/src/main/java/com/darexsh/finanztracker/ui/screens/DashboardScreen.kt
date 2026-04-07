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

@Composable
fun DashboardScreen(state: TrackerState) {
    val activeBookings = state.bookings.filter { it.userId == state.activeUserId }
    val income = activeBookings.filter { it.txType == TxType.INCOME }.sumOf { it.amount }
    val expense = activeBookings.filter { it.txType == TxType.EXPENSE }.sumOf { it.amount }
    val balance = income - expense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.screen_overview), style = MaterialTheme.typography.headlineSmall)
        SummaryCard(stringResource(R.string.summary_current_balance), stringResource(R.string.format_eur, balance))
        SummaryCard(stringResource(R.string.summary_income), stringResource(R.string.format_eur, income))
        SummaryCard(stringResource(R.string.summary_expense), stringResource(R.string.format_eur, expense))
    }
}

@Composable
private fun SummaryCard(title: String, value: String) {
    Card {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
