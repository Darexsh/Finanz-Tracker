package com.darexsh.finanztracker.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.domain.CategoryMutationStatus
import com.darexsh.finanztracker.domain.FinanceCatalog
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.CurrencyPreference
import com.darexsh.finanztracker.model.DateFormatPreference
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import com.darexsh.finanztracker.ui.canonicalDateToDisplay
import com.darexsh.finanztracker.ui.displayDateToCanonical
import com.darexsh.finanztracker.ui.FinanceLabelLocalizer
import com.darexsh.finanztracker.ui.formatCurrencyValue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class TypeFilter { ALL, EXPENSE, INCOME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    state: TrackerState,
    keepDateAfterSave: Boolean = true,
    dateFormat: DateFormatPreference = DateFormatPreference.DMY_DOT,
    currency: CurrencyPreference = CurrencyPreference.EUR,
    sortNewestFirst: Boolean = true,
    categorySuggestionsEnabled: Boolean = true,
    onAddBooking: (draft: BookingDraft) -> Unit,
    onUpdateBooking: (bookingId: String, draft: BookingDraft) -> Unit,
    onDeleteBooking: (bookingId: String) -> Unit,
    onDeleteBookings: (bookingIds: Set<String>) -> Unit,
    onSetBookingTaxDeclaration: (bookingId: String, taxDeclaration: Boolean) -> Unit,
    onAddCustomCategory: (name: String) -> CategoryMutationStatus,
    onRenameCustomCategory: (currentName: String, newName: String) -> CategoryMutationStatus,
    onDeleteCustomCategory: (name: String) -> CategoryMutationStatus
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val todayCanonical = remember { SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(Date()) }
    var bookingDate by remember(dateFormat) { mutableStateOf(canonicalDateToDisplay(todayCanonical, dateFormat)) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    val defaultCategory = stringResource(R.string.default_category)
    val defaultAccount = stringResource(R.string.default_account)
    val defaultCategoryStorage = remember(defaultCategory) { FinanceCatalog.normalizeCategory(defaultCategory) }
    val defaultAccountStorage = remember(defaultAccount) {
        FinanceCatalog.accounts.firstOrNull() ?: defaultAccount
    }
    val uiLocale = remember(context) {
        val locales = context.resources.configuration.locales
        if (!locales.isEmpty) locales[0] else Locale.getDefault()
    }
    val localizeCategoryLabel: (String) -> String = { value ->
        FinanceLabelLocalizer.localizeCategory(value, uiLocale)
    }
    val localizeAccountLabel: (String) -> String = { value ->
        FinanceLabelLocalizer.localizeAccount(value, uiLocale)
    }
    var category by remember(defaultCategoryStorage) { mutableStateOf(defaultCategoryStorage) }
    var account by remember(defaultAccountStorage) { mutableStateOf(defaultAccountStorage) }
    var note by remember { mutableStateOf("") }
    var txType by remember { mutableStateOf(TxType.EXPENSE) }
    var taxDeclaration by remember { mutableStateOf(false) }
    var editingBookingId by remember { mutableStateOf<String?>(null) }
    var categoryManuallyOverridden by remember { mutableStateOf(false) }
    var lastAutoCategory by remember { mutableStateOf<String?>(null) }

    var filterSearch by remember { mutableStateOf("") }
    var filterMonth by remember { mutableStateOf("") }
    var filterYear by remember { mutableStateOf(currentYear.toString()) }
    var filterCategory by remember { mutableStateOf("") }
    var filterAccount by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf(TypeFilter.ALL) }
    val selectedBookingIds = remember { mutableStateListOf<String>() }
    val allCategoriesLabel = stringResource(R.string.filter_all_categories)
    val allAccountsLabel = stringResource(R.string.filter_all_accounts)
    val allMonthsLabel = stringResource(R.string.filter_all_months)
    val allYearsLabel = stringResource(R.string.filter_all_years)
    val validationDateRequired = stringResource(R.string.validation_error_date_required)
    val validationDescriptionRequired = stringResource(R.string.validation_error_description_required)
    val validationAmountRequired = stringResource(R.string.validation_error_amount_required)
    val categoryErrorEmpty = stringResource(R.string.category_error_empty_name)
    val categoryErrorExists = stringResource(R.string.category_error_exists)
    val categoryErrorBuiltinRename = stringResource(R.string.category_error_builtin_rename)
    val categoryErrorBuiltinDelete = stringResource(R.string.category_error_builtin_delete)
    val categoryErrorNotFound = stringResource(R.string.category_error_not_found)
    var showRenameCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var categoryDialogInput by remember { mutableStateOf("") }
    var categoryInfoMessage by remember { mutableStateOf<String?>(null) }
    val monthFilterOptions = remember {
        (1..12).map { it.toString().padStart(2, '0') }
    }
    val currentYear = remember { SimpleDateFormat("yyyy", Locale.GERMANY).format(Date()) }

    val activeBookings = remember(state.bookings, state.activeUserId, sortNewestFirst) {
        state.bookings
            .asSequence()
            .filter { it.userId == state.activeUserId }
            .sortedWith(if (sortNewestFirst) {
                compareByDescending<Booking> { bookingDateSortKey(it.date) }.thenByDescending { it.createdAt }
            } else {
                compareBy<Booking> { bookingDateSortKey(it.date) }.thenBy { it.createdAt }
            })
            .toList()
    }
    val activeBookingIds = remember(activeBookings) { activeBookings.map { it.id }.toSet() }
    val normalizedSearch = remember(filterSearch) { filterSearch.trim().lowercase() }
    val searchableBookingText = remember(activeBookings) {
        activeBookings.associate { booking ->
            booking.id to buildString {
                append(booking.description.lowercase())
                append(" ")
                append(booking.note.lowercase())
                append(" ")
                append(booking.category.lowercase())
                append(" ")
                append(booking.account.lowercase())
            }
        }
    }
    val categoryOptions = remember(state.customCategories, activeBookings, defaultCategoryStorage) {
        (FinanceCatalog.categories + state.customCategories + activeBookings.map { it.category } + defaultCategoryStorage)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }
    val accountOptions = remember(activeBookings, defaultAccountStorage) {
        (FinanceCatalog.accounts + activeBookings.map { it.account } + defaultAccountStorage)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }
    val yearFilterOptions = remember(activeBookings, currentYear) {
        val years = activeBookings.mapNotNull { booking ->
            booking.date.split(".").getOrNull(2)?.trim()?.takeIf { it.length == 4 }
        }.distinct().toMutableList()
        if (!years.contains(currentYear)) years.add(currentYear)
        years.sortedDescending()
    }

    val filteredBookings = remember(
        activeBookings,
        searchableBookingText,
        normalizedSearch,
        filterMonth,
        filterYear,
        filterCategory,
        filterAccount,
        filterType
    ) {
        activeBookings.filter { booking ->
            val typeMatches = when (filterType) {
                TypeFilter.ALL -> true
                TypeFilter.EXPENSE -> booking.txType == TxType.EXPENSE
                TypeFilter.INCOME -> booking.txType == TxType.INCOME
            }
            val parts = booking.date.split(".")
            val bookingMonth = parts.getOrNull(1).orEmpty()
            val bookingYear = parts.getOrNull(2).orEmpty()
            val textBlob = searchableBookingText[booking.id].orEmpty()

            typeMatches &&
                (normalizedSearch.isBlank() || textBlob.contains(normalizedSearch)) &&
                (filterMonth.isBlank() || bookingMonth == filterMonth) &&
                (filterYear.isBlank() || bookingYear == filterYear) &&
                (filterCategory.isBlank() || booking.category.equals(filterCategory, ignoreCase = true)) &&
                (filterAccount.isBlank() || booking.account.equals(filterAccount, ignoreCase = true))
        }
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

    fun resetForm(resetDateToToday: Boolean = true) {
        if (resetDateToToday) {
            bookingDate = canonicalDateToDisplay(todayCanonical, dateFormat)
        }
        description = ""
        amount = ""
        dateError = null
        descriptionError = null
        amountError = null
        category = defaultCategoryStorage
        account = defaultAccountStorage
        note = ""
        txType = TxType.EXPENSE
        taxDeclaration = false
        editingBookingId = null
        categoryManuallyOverridden = false
        lastAutoCategory = null
    }

    fun messageForStatus(status: CategoryMutationStatus, action: String): String? {
        return when (status) {
            CategoryMutationStatus.SUCCESS -> null
            CategoryMutationStatus.EMPTY_NAME -> categoryErrorEmpty
            CategoryMutationStatus.ALREADY_EXISTS -> categoryErrorExists
            CategoryMutationStatus.BUILT_IN_BLOCKED -> if (action == "rename") {
                categoryErrorBuiltinRename
            } else {
                categoryErrorBuiltinDelete
            }
            CategoryMutationStatus.NOT_FOUND -> categoryErrorNotFound
        }
    }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val canonical = displayDateToCanonical(bookingDate, dateFormat)
        val parsed = runCatching { canonical?.let { SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(it) } }.getOrNull()
        if (parsed != null) calendar.time = parsed

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val canonicalDate = String.format(Locale.GERMANY, "%02d.%02d.%04d", dayOfMonth, month + 1, year)
                bookingDate = canonicalDateToDisplay(canonicalDate, dateFormat)
                dateError = null
                focusManager.clearFocus(force = true)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
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
                        onValueChange = {
                            bookingDate = it
                            dateError = null
                        },
                        label = { Text(stringResource(R.string.label_date)) },
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = stringResource(R.string.label_date)
                                )
                            }
                        },
                        singleLine = true,
                        isError = dateError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (dateError != null) {
                        Text(
                            text = dateError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = { newValue ->
                            description = newValue
                            descriptionError = null
                            if (categorySuggestionsEnabled && !categoryManuallyOverridden) {
                                val suggestion = FinanceCatalog.suggestCategory(newValue, categoryOptions) ?: return@OutlinedTextField
                                if (category == defaultCategoryStorage || category.equals(lastAutoCategory, ignoreCase = true)) {
                                    category = suggestion
                                    lastAutoCategory = suggestion
                                }
                            }
                        },
                        label = { Text(stringResource(R.string.label_description)) },
                        isError = descriptionError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (descriptionError != null) {
                        Text(
                            text = descriptionError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            amountError = null
                        },
                        label = { Text(stringResource(R.string.label_amount)) },
                        isError = amountError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (amountError != null) {
                        Text(
                            text = amountError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
                            readOnly = true,
                            displayValue = localizeCategoryLabel,
                            modifier = Modifier.weight(1f)
                        )
                        SelectableField(
                            value = account,
                            onValueChange = { account = it.trim() },
                            label = { Text(stringResource(R.string.label_account)) },
                            options = accountOptions,
                            readOnly = true,
                            displayValue = localizeAccountLabel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val selectedCategory = category.trim()
                    val isSelectedCategoryCustom = state.customCategories.any {
                        it.equals(selectedCategory, ignoreCase = true)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val result = onAddCustomCategory(category)
                                if (result == CategoryMutationStatus.SUCCESS) {
                                    category = category.trim()
                                    categoryManuallyOverridden = true
                                } else {
                                    categoryInfoMessage = messageForStatus(result, action = "add")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.button_new_category),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val selected = selectedCategory
                                val isCustom = isSelectedCategoryCustom
                                if (selected.isBlank()) {
                                    Toast.makeText(context, categoryErrorNotFound, Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                if (!isCustom) {
                                    Toast.makeText(context, categoryErrorBuiltinRename, Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                categoryDialogInput = selected
                                showRenameCategoryDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isSelectedCategoryCustom) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                }
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.button_rename_category_short),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = {
                                val selected = selectedCategory
                                val isCustom = isSelectedCategoryCustom
                                if (selected.isBlank()) {
                                    Toast.makeText(context, categoryErrorNotFound, Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (!isCustom) {
                                    Toast.makeText(context, categoryErrorBuiltinDelete, Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                showDeleteCategoryDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelectedCategoryCustom) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                },
                                contentColor = if (isSelectedCategoryCustom) {
                                    MaterialTheme.colorScheme.onError
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                                }
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.button_delete_category_short),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
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
                                val parsed = amount.replace(',', '.').toDoubleOrNull()
                                val canonicalDate = displayDateToCanonical(bookingDate, dateFormat)
                                val missingDate = canonicalDate.isNullOrBlank()
                                val missingDescription = description.isBlank()
                                val invalidAmount = amount.isBlank() || parsed == null

                                dateError = if (missingDate) validationDateRequired else null
                                descriptionError = if (missingDescription) validationDescriptionRequired else null
                                amountError = if (invalidAmount) validationAmountRequired else null

                                if (missingDate || missingDescription || invalidAmount) return@Button

                                if (editingBookingId != null) {
                                    onUpdateBooking(
                                        editingBookingId!!,
                                        BookingDraft(
                                            date = canonicalDate.orEmpty(),
                                            description = description,
                                            amount = parsed!!,
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
                                            date = canonicalDate.orEmpty(),
                                            description = description,
                                            amount = parsed!!,
                                            category = category,
                                            txType = txType,
                                            account = account,
                                            note = note,
                                            taxDeclaration = taxDeclaration
                                        )
                                    )
                                }

                                resetForm(resetDateToToday = !keepDateAfterSave)
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
                            onClick = { resetForm(resetDateToToday = true) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
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
                            value = if (filterMonth.isBlank()) allMonthsLabel else filterMonth,
                            onValueChange = { value ->
                                filterMonth = if (value == allMonthsLabel) "" else value
                            },
                            label = { Text(stringResource(R.string.filter_month)) },
                            options = listOf(allMonthsLabel) + monthFilterOptions,
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        SelectableField(
                            value = if (filterYear.isBlank()) allYearsLabel else filterYear,
                            onValueChange = { value ->
                                filterYear = if (value == allYearsLabel) "" else value
                            },
                            label = { Text(stringResource(R.string.filter_year)) },
                            options = listOf(allYearsLabel) + yearFilterOptions,
                            readOnly = true,
                            preferAbove = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        SelectableField(
                            value = if (filterCategory.isBlank()) allCategoriesLabel else filterCategory,
                            onValueChange = { value ->
                                filterCategory = if (value == allCategoriesLabel) "" else value
                            },
                            label = { Text(stringResource(R.string.filter_category)) },
                            options = listOf(allCategoriesLabel) + categoryOptions,
                            readOnly = true,
                            displayValue = localizeCategoryLabel,
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
                            displayValue = localizeAccountLabel,
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
                    OutlinedButton(
                        onClick = {
                            filterSearch = ""
                            filterMonth = ""
                            filterYear = currentYear.toString()
                            filterCategory = ""
                            filterAccount = ""
                            filterType = TypeFilter.ALL
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.button_reset_filters))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
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

        items(
            items = filteredBookings,
            key = { it.id },
            contentType = { "booking-row" }
        ) { booking ->
            BookingRow(
                modifier = Modifier,
                booking = booking,
                dateFormat = dateFormat,
                currency = currency,
                localizeCategoryLabel = localizeCategoryLabel,
                localizeAccountLabel = localizeAccountLabel,
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
                    bookingDate = canonicalDateToDisplay(booking.date, dateFormat)
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

    if (showRenameCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showRenameCategoryDialog = false },
            title = { Text(stringResource(R.string.dialog_rename_category_title)) },
            text = {
                OutlinedTextField(
                    value = categoryDialogInput,
                    onValueChange = { categoryDialogInput = it },
                    label = { Text(stringResource(R.string.dialog_category_name_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val currentCategory = category.trim()
                        val result = onRenameCustomCategory(currentCategory, categoryDialogInput)
                        if (result == CategoryMutationStatus.SUCCESS) {
                            category = categoryDialogInput.trim()
                            categoryManuallyOverridden = true
                            showRenameCategoryDialog = false
                        } else {
                            categoryInfoMessage = messageForStatus(result, action = "rename")
                        }
                    }
                ) {
                    Text(stringResource(R.string.button_update))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameCategoryDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }

    if (showDeleteCategoryDialog) {
        val selectedCategory = category.trim()
        val usageCount = activeBookings.count { it.category.equals(selectedCategory, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_category_title)) },
            text = {
                Text(
                    if (usageCount > 0) {
                        stringResource(R.string.dialog_delete_category_message_with_usage, selectedCategory, usageCount)
                    } else {
                        stringResource(R.string.dialog_delete_category_message, selectedCategory)
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val result = onDeleteCustomCategory(selectedCategory)
                        if (result == CategoryMutationStatus.SUCCESS) {
                            category = defaultCategoryStorage
                            categoryManuallyOverridden = false
                            showDeleteCategoryDialog = false
                        } else {
                            categoryInfoMessage = messageForStatus(result, action = "delete")
                        }
                    }
                ) {
                    Text(stringResource(R.string.button_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCategoryDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }

    if (categoryInfoMessage != null) {
        AlertDialog(
            onDismissRequest = { categoryInfoMessage = null },
            title = { Text(stringResource(R.string.dialog_category_info_title)) },
            text = { Text(categoryInfoMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { categoryInfoMessage = null }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }
}

private fun bookingDateSortKey(date: String): Int {
    val parts = date.split(".")
    if (parts.size != 3) return Int.MIN_VALUE

    val day = parts[0].toIntOrNull() ?: return Int.MIN_VALUE
    val month = parts[1].toIntOrNull() ?: return Int.MIN_VALUE
    val year = parts[2].toIntOrNull() ?: return Int.MIN_VALUE

    if (day !in 1..31 || month !in 1..12 || year !in 1..9999) return Int.MIN_VALUE
    return (year * 10_000) + (month * 100) + day
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
    displayValue: (String) -> String = { it },
    preferAbove: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
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
    Column(modifier = modifier) {
        OutlinedTextField(
            value = displayValue(value),
            onValueChange = {
                if (!readOnly) {
                    onValueChange(it)
                    expanded = true
                }
            },
            label = label,
            readOnly = readOnly,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    expanded = !expanded
                    if (!expanded) focusManager.clearFocus(force = true)
                }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) expanded = true
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus(force = true)
            },
            offset = DpOffset(0.dp, if (preferAbove) (-240).dp else 0.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            properties = PopupProperties(focusable = false)
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                shownOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(displayValue(option)) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                            focusManager.clearFocus(force = true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingRow(
    modifier: Modifier = Modifier,
    booking: Booking,
    dateFormat: DateFormatPreference,
    currency: CurrencyPreference,
    localizeCategoryLabel: (String) -> String,
    localizeAccountLabel: (String) -> String,
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

    val cardContainerColor = if (booking.taxDeclaration) {
        Color(0xFFFFF6CC)
    } else {
        MaterialTheme.colorScheme.surface
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = onSelectChanged
                    )
                    Text(
                        stringResource(R.string.label_select),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.label_tax_declaration_short),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Checkbox(
                        checked = booking.taxDeclaration,
                        onCheckedChange = onTaxDeclarationChanged
                    )
                }
            }
            Text("${canonicalDateToDisplay(booking.date, dateFormat)} · ${booking.description}")
            Text(
                "${localizeCategoryLabel(booking.category)} · ${localizeAccountLabel(booking.account)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "$txTypeLabel · ${formatCurrencyValue(booking.amount, currency)}",
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
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.button_delete))
                }
            }
        }
    }
}
