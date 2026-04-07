package com.darexsh.finanztracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darexsh.finanztracker.data.StateRepository
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class AppViewModel(
    private val repository: StateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(repository.loadState())
    val state: StateFlow<TrackerState> = _state.asStateFlow()

    init {
        autoLoadFromSyncOnStart()
    }

    fun addBooking(
        description: String,
        amount: Double,
        category: String,
        txType: TxType,
        account: String,
        note: String,
        taxDeclaration: Boolean
    ) {
        val current = _state.value
        val activeUser = current.users.firstOrNull { it.id == current.activeUserId } ?: return
        val booking = Booking(
            id = "b_" + Random.nextLong().toString(),
            userId = activeUser.id,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(Date()),
            description = description.trim(),
            category = category.trim().ifBlank { "Other" },
            txType = txType,
            amount = amount,
            account = account.trim().ifBlank { "Checking Account" },
            note = note,
            taxDeclaration = taxDeclaration,
            createdAt = System.currentTimeMillis()
        )

        updateState(current.copy(bookings = listOf(booking) + current.bookings))
    }

    fun setActiveUser(userId: String) {
        val current = _state.value
        if (current.users.none { it.id == userId }) return
        updateState(current.copy(activeUserId = userId))
    }

    fun setSyncFolderUri(uri: String) {
        val current = _state.value
        updateState(current.copy(syncFolderUri = uri), writeSyncFile = false)

        viewModelScope.launch(Dispatchers.IO) {
            val synced = repository.loadSyncState(uri)
            if (synced != null) {
                val merged = synced.copy(syncFolderUri = uri)
                _state.value = merged
                repository.saveState(merged)
            }
            // Important safety rule:
            // Do not write anything on folder selection when loading fails or file is absent.
            // First write should happen only after an explicit data change in app.
        }
    }

    fun clearSyncFolderUri() {
        val current = _state.value
        updateState(current.copy(syncFolderUri = null), writeSyncFile = false)
    }

    private fun autoLoadFromSyncOnStart() {
        val syncUri = _state.value.syncFolderUri ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val synced = repository.loadSyncState(syncUri) ?: return@launch
            val merged = synced.copy(syncFolderUri = syncUri)
            _state.value = merged
            repository.saveState(merged)
        }
    }

    private fun updateState(newState: TrackerState, writeSyncFile: Boolean = true) {
        _state.value = newState
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveState(newState)

            val syncUri = newState.syncFolderUri
            if (writeSyncFile && !syncUri.isNullOrBlank()) {
                repository.saveSyncState(syncUri, newState)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: StateRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(repository) as T
        }
    }
}
