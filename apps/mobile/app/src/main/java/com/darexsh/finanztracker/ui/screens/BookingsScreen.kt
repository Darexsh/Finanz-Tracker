package com.darexsh.finanztracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    state: TrackerState,
    onAddBooking: (
        description: String,
        amount: Double,
        category: String,
        txType: TxType,
        account: String,
        note: String,
        taxDeclaration: Boolean
    ) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val defaultCategory = stringResource(R.string.default_category)
    val defaultAccount = stringResource(R.string.default_account)
    var category by remember(defaultCategory) { mutableStateOf(defaultCategory) }
    var account by remember(defaultAccount) { mutableStateOf(defaultAccount) }
    var note by remember { mutableStateOf("") }
    var txType by remember { mutableStateOf(TxType.EXPENSE) }

    val activeBookings = state.bookings.filter { it.userId == state.activeUserId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(stringResource(R.string.screen_bookings))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.label_description)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(stringResource(R.string.label_amount)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(stringResource(R.string.label_category)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = account,
                onValueChange = { account = it },
                label = { Text(stringResource(R.string.label_account)) },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(stringResource(R.string.label_note)) },
            modifier = Modifier.fillMaxWidth()
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                selected = txType == TxType.EXPENSE,
                onClick = { txType = TxType.EXPENSE }
            ) { Text(stringResource(R.string.type_expense)) }
            SegmentedButton(
                shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                selected = txType == TxType.INCOME,
                onClick = { txType = TxType.INCOME }
            ) { Text(stringResource(R.string.type_income)) }
        }

        Button(onClick = {
            val parsed = amount.replace(',', '.').toDoubleOrNull() ?: return@Button
            if (description.isBlank()) return@Button
            onAddBooking(description, parsed, category, txType, account, note, false)
            description = ""
            amount = ""
            note = ""
        }) {
            Text(stringResource(R.string.button_save))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(activeBookings, key = { it.id }) { booking ->
                BookingRow(booking)
            }
        }
    }
}

@Composable
private fun BookingRow(booking: Booking) {
    val txTypeLabel = if (booking.txType == TxType.INCOME) {
        stringResource(R.string.type_income)
    } else {
        stringResource(R.string.type_expense)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${booking.date} · ${booking.description}")
            Text("${booking.category} · ${booking.account}")
            Text(stringResource(R.string.format_booking_type_amount, txTypeLabel, booking.amount))
        }
    }
}
