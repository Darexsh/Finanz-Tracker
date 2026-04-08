package com.darexsh.finanztracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.domain.FinanceCatalog
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class TypeFilter { ALL, EXPENSE, INCOME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    state: TrackerState,
    onAddBooking: (draft: BookingDraft) -> Unit,
    onUpdateBooking: (bookingId: String, draft: BookingDraft) -> Unit,
    onDeleteBooking: (bookingId: String) -> Unit,
    onDeleteBookings: (bookingIds: Set<String>) -> Unit,
    onSetBookingTaxDeclaration: (bookingId: String, taxDeclaration: Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val todayDate = remember { SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(Date()) }
    var bookingDate by remember { mutableStateOf(todayDate) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val defaultCategory = stringResource(R.string.default_category)
    val defaultAccount = stringResource(R.string.default_account)
    var category by remember(defaultCategory) { mutableStateOf(defaultCategory) }
    var account by remember(defaultAccount) { mutableStateOf(defaultAccount) }
    var note by remember { mutableStateOf("") }
    var txType by remember { mutableStateOf(TxType.EXPENSE) }
    var taxDeclaration by remember { mutableStateOf(false) }
    var editingBookingId by remember { mutableStateOf<String?>(null) }
    var categoryManuallyOverridden by remember { mutableStateOf(false) }
    var lastAutoCategory by remember { mutableStateOf<String?>(null) }

    var filterSearch by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("") }
    var filterAccount by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf(TypeFilter.ALL) }
    val selectedBookingIds = remember { mutableStateListOf<String>() }
    val allCategoriesLabel = stringResource(R.string.filter_all_categories)
    val allAccountsLabel = stringResource(R.string.filter_all_accounts)

    val activeBookings = state.bookings.filter { it.userId == state.activeUserId }.sortedByDescending { it.createdAt }
    val activeBookingIds = activeBookings.map { it.id }.toSet()
    val normalizedSearch = filterSearch.trim().lowercase()
    val categoryOptions = remember(state.customCategories, activeBookings, defaultCategory) {
        (FinanceCatalog.categories + state.customCategories + activeBookings.map { it.category } + defaultCategory)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }
    val accountOptions = remember(activeBookings, defaultAccount) {
        (FinanceCatalog.accounts + activeBookings.map { it.account } + defaultAccount)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }

    val filteredBookings = activeBookings.filter { booking ->
        val typeMatches = when (filterType) {
            TypeFilter.ALL -> true
            TypeFilter.EXPENSE -> booking.txType == TxType.EXPENSE
            TypeFilter.INCOME -> booking.txType == TxType.INCOME
        }

        val textBlob = buildString {
            append(booking.description.lowercase())
            append(" ")
            append(booking.note.lowercase())
            append(" ")
            append(booking.category.lowercase())
            append(" ")
            append(booking.account.lowercase())
        }

        typeMatches &&
            (normalizedSearch.isBlank() || textBlob.contains(normalizedSearch)) &&
            (filterCategory.isBlank() || booking.category.equals(filterCategory, ignoreCase = true)) &&
            (filterAccount.isBlank() || booking.account.equals(filterAccount, ignoreCase = true))
    }

    LaunchedEffect(state.activeUserId) {
        selectedBookingIds.clear()
        editingBookingId = null
    }

    LaunchedEffect(activeBookingIds) {
        selectedBookingIds.retainAll(activeBookingIds)
        if (editingBookingId != null && !activeBookingIds.contains(editingBookingId)) {
            editingBookingId = null
        }
    }

    fun resetForm() {
        bookingDate = todayDate
        description = ""
        amount = ""
        category = defaultCategory
        account = defaultAccount
        note = ""
        txType = TxType.EXPENSE
        taxDeclaration = false
        editingBookingId = null
        categoryManuallyOverridden = false
        lastAutoCategory = null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                stringResource(R.string.screen_bookings),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        stringResource(R.string.bookings_section_new),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.bookings_help_new),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = bookingDate,
                        onValueChange = { bookingDate = it },
                        label = { Text(stringResource(R.string.label_date)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { newValue ->
                            description = newValue
                            if (!categoryManuallyOverridden) {
                                val suggestion = FinanceCatalog.suggestCategory(newValue, categoryOptions) ?: return@OutlinedTextField
                                if (category == defaultCategory || category.equals(lastAutoCategory, ignoreCase = true)) {
                                    category = suggestion
                                    lastAutoCategory = suggestion
                                }
                            }
                        },
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
                        SelectableField(
                            value = category,
                            onValueChange = {
                                val next = it.trim()
                                category = next
                                if (!next.equals(lastAutoCategory, ignoreCase = true)) {
                                    categoryManuallyOverridden = true
                                }
                            },
                            label = { Text(stringResource(R.string.label_category)) },
                            options = categoryOptions,
                            readOnly = false,
                            modifier = Modifier.weight(1f)
                        )
                        SelectableField(
                            value = account,
                            onValueChange = { account = it.trim() },
                            label = { Text(stringResource(R.string.label_account)) },
                            options = accountOptions,
                            readOnly = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.label_note)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = taxDeclaration,
                            onCheckedChange = { taxDeclaration = it }
                        )
                        Text(
                            stringResource(R.string.label_tax_declaration),
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            selected = txType == TxType.EXPENSE,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            onClick = { txType = TxType.EXPENSE }
                        ) { Text(stringResource(R.string.type_expense)) }
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            selected = txType == TxType.INCOME,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            onClick = { txType = TxType.INCOME }
                        ) { Text(stringResource(R.string.type_income)) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                val parsed = amount.replace(',', '.').toDoubleOrNull() ?: return@Button
                                if (description.isBlank()) return@Button

                                if (editingBookingId != null) {
                                    onUpdateBooking(
                                        editingBookingId!!,
                                        BookingDraft(
                                            date = bookingDate,
                                            description = description,
                                            amount = parsed,
                                            category = category,
                                            txType = txType,
                                            account = account,
                                            note = note,
                                            taxDeclaration = taxDeclaration
                                        )
                                    )
                                } else {
                                    onAddBooking(
                                        BookingDraft(
                                            date = bookingDate,
                                            description = description,
                                            amount = parsed,
                                            category = category,
                                            txType = txType,
                                            account = account,
                                            note = note,
                                            taxDeclaration = taxDeclaration
                                        )
                                    )
                                }

                                resetForm()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (editingBookingId == null) {
                                    stringResource(R.string.button_save)
                                } else {
                                    stringResource(R.string.button_update)
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = { resetForm() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.button_clear))
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        stringResource(R.string.bookings_section_filter),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.bookings_help_filter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = filterSearch,
                        onValueChange = { filterSearch = it },
                        label = { Text(stringResource(R.string.filter_search)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        SelectableField(
                            value = if (filterCategory.isBlank()) allCategoriesLabel else filterCategory,
                            onValueChange = { value ->
                                filterCategory = if (value == allCategoriesLabel) "" else value
                            },
                            label = { Text(stringResource(R.string.filter_category)) },
                            options = listOf(allCategoriesLabel) + categoryOptions,
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        SelectableField(
                            value = if (filterAccount.isBlank()) allAccountsLabel else filterAccount,
                            onValueChange = { value ->
                                filterAccount = if (value == allAccountsLabel) "" else value
                            },
                            label = { Text(stringResource(R.string.filter_account)) },
                            options = listOf(allAccountsLabel) + accountOptions,
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                            selected = filterType == TypeFilter.ALL,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            onClick = { filterType = TypeFilter.ALL }
                        ) { Text(stringResource(R.string.filter_type_all)) }

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                            selected = filterType == TypeFilter.EXPENSE,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            onClick = { filterType = TypeFilter.EXPENSE }
                        ) { Text(stringResource(R.string.type_expense)) }

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                            selected = filterType == TypeFilter.INCOME,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                inactiveBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            onClick = { filterType = TypeFilter.INCOME }
                        ) { Text(stringResource(R.string.type_income)) }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.bookings_section_list),
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (selectedBookingIds.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.bookings_selected_count, selectedBookingIds.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                val idsToDelete = selectedBookingIds.toSet()
                                onDeleteBookings(selectedBookingIds.toSet())
                                if (editingBookingId != null && idsToDelete.contains(editingBookingId)) {
                                    resetForm()
                                }
                                selectedBookingIds.clear()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_delete_selected, selectedBookingIds.size))
                        }
                    }
                }
            }
        }

        items(filteredBookings, key = { it.id }) { booking ->
            BookingRow(
                booking = booking,
                selected = selectedBookingIds.contains(booking.id),
                onSelectChanged = { checked ->
                    if (checked) {
                        if (!selectedBookingIds.contains(booking.id)) selectedBookingIds.add(booking.id)
                    } else {
                        selectedBookingIds.remove(booking.id)
                    }
                },
                onEdit = {
                    editingBookingId = booking.id
                    bookingDate = booking.date
                    description = booking.description
                    amount = formatAmountForInput(booking.amount)
                    category = booking.category
                    account = booking.account
                    note = booking.note
                    txType = booking.txType
                    taxDeclaration = booking.taxDeclaration
                    categoryManuallyOverridden = true
                    lastAutoCategory = null
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                onDelete = {
                    onDeleteBooking(booking.id)
                    selectedBookingIds.remove(booking.id)
                    if (editingBookingId == booking.id) {
                        resetForm()
                    }
                },
                onTaxDeclarationChanged = { checked ->
                    onSetBookingTaxDeclaration(booking.id, checked)
                    if (editingBookingId == booking.id) {
                        taxDeclaration = checked
                    }
                }
            )
        }
    }
}

private fun formatAmountForInput(value: Double): String =
    String.format(Locale.GERMANY, "%.2f", value)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    options: List<String>,
    readOnly: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val shownOptions = if (readOnly) {
        options
    } else {
        val query = value.trim().lowercase()
        val hasExactOption = options.any { it.equals(value.trim(), ignoreCase = true) }
        if (query.isBlank() || hasExactOption) {
            options
        } else {
            options.filter { it.lowercase().contains(query) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (!readOnly) {
                    onValueChange(it)
                    expanded = true
                }
            },
            label = label,
            readOnly = readOnly,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                shownOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingRow(
    booking: Booking,
    selected: Boolean,
    onSelectChanged: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTaxDeclarationChanged: (Boolean) -> Unit
) {
    val txTypeLabel = if (booking.txType == TxType.INCOME) {
        stringResource(R.string.type_income)
    } else {
        stringResource(R.string.type_expense)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectChanged
                )
                Text(
                    stringResource(R.string.label_tax_declaration_short),
                    modifier = Modifier.padding(top = 12.dp)
                )
                Checkbox(
                    checked = booking.taxDeclaration,
                    onCheckedChange = onTaxDeclarationChanged
                )
            }
            Text("${booking.date} · ${booking.description}")
            Text(
                "${booking.category} · ${booking.account}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                stringResource(R.string.format_booking_type_amount, txTypeLabel, booking.amount),
                style = MaterialTheme.typography.titleSmall
            )
            if (booking.note.isNotBlank()) {
                Text(
                    booking.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.button_edit))
                }
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.button_delete))
                }
            }
        }
    }
}
