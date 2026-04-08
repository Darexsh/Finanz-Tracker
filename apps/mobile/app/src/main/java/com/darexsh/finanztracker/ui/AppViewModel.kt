package com.darexsh.finanztracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.domain.TrackerService
import com.darexsh.finanztracker.model.TrackerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val service: TrackerService
) : ViewModel() {

    private val _state = MutableStateFlow(service.loadState())
    val state: StateFlow<TrackerState> = _state.asStateFlow()

    init {
        autoLoadFromSyncOnStart()
    }

    fun addUser(name: String): Boolean {
        val current = _state.value
        val newState = service.addUser(current, name) ?: return false
        updateState(newState)
        return true
    }

    fun renameActiveUser(newName: String): Boolean {
        val current = _state.value
        val newState = service.renameActiveUser(current, newName) ?: return false
        updateState(newState)
        return true
    }

    fun deleteActiveUser(): Boolean {
        val current = _state.value
        val newState = service.deleteActiveUser(current) ?: return false
        updateState(newState)
        return true
    }

    fun addBooking(draft: BookingDraft) {
        val current = _state.value
        val newState = service.addBooking(current, draft) ?: return
        updateState(newState)
    }

    fun updateBooking(bookingId: String, draft: BookingDraft) {
        val current = _state.value
        val newState = service.updateBooking(current, bookingId, draft) ?: return
        updateState(newState)
    }

    fun deleteBooking(bookingId: String) {
        val current = _state.value
        val newState = service.deleteBooking(current, bookingId) ?: return
        updateState(newState)
    }

    fun deleteBookings(bookingIds: Set<String>) {
        val current = _state.value
        val newState = service.deleteBookings(current, bookingIds) ?: return
        updateState(newState)
    }

    fun setBookingTaxDeclaration(bookingId: String, taxDeclaration: Boolean) {
        val current = _state.value
        val newState = service.setBookingTaxDeclaration(current, bookingId, taxDeclaration)
        updateState(newState)
    }

    fun setActiveUser(userId: String) {
        val current = _state.value
        val newState = service.setActiveUser(current, userId) ?: return
        updateState(newState)
    }

    fun setSyncFolderUri(uri: String) {
        val current = _state.value
        val localState = service.setSyncFolderUri(current, uri)
        updateState(localState, writeSyncFile = false)

        viewModelScope.launch {
            val synced = service.loadSyncState(uri)
            if (synced != null) {
                val merged = synced.copy(syncFolderUri = uri)
                _state.value = merged
                service.persistLocalState(merged)
            }
            // Important safety rule:
            // Do not write anything on folder selection when loading fails or file is absent.
            // First write should happen only after an explicit data change in app.
        }
    }

    fun clearSyncFolderUri() {
        val current = _state.value
        val newState = service.clearSyncFolderUri(current)
        updateState(newState, writeSyncFile = false)
    }

    private fun autoLoadFromSyncOnStart() {
        viewModelScope.launch {
            val merged = service.autoLoadFromSyncOnStart(_state.value) ?: return@launch
            _state.value = merged
            service.persistLocalState(merged)
        }
    }

    private fun updateState(newState: TrackerState, writeSyncFile: Boolean = true) {
        _state.value = newState
        viewModelScope.launch {
            service.persistState(newState, writeSyncFile = writeSyncFile)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val service: TrackerService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(service) as T
        }
    }
}
