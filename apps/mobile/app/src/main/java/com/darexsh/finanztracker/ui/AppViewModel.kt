package com.darexsh.finanztracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.domain.CategoryMutationStatus
import com.darexsh.finanztracker.domain.TrackerService
import com.darexsh.finanztracker.model.AppSettings
import com.darexsh.finanztracker.model.TrackerState
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(
    private val service: TrackerService
) : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val _state = MutableStateFlow(service.loadState())
    val state: StateFlow<TrackerState> = _state.asStateFlow()
    private var syncAutoRefreshJob: Job? = null
    private var syncAutoRefreshUri: String? = null
    private val syncAutoRefreshIntervalMs = 5_000L

    init {
        autoLoadFromSyncOnStart()
        startSyncAutoRefreshIfConfigured(_state.value.syncFolderUri)
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

    fun addCustomCategory(name: String): CategoryMutationStatus {
        val current = _state.value
        val result = service.addCustomCategory(current, name)
        result.state?.let { updateState(it) }
        return result.status
    }

    fun renameCustomCategory(currentName: String, newName: String): CategoryMutationStatus {
        val current = _state.value
        val result = service.renameCustomCategory(current, currentName, newName)
        result.state?.let { updateState(it) }
        return result.status
    }

    fun deleteCustomCategory(name: String): CategoryMutationStatus {
        val current = _state.value
        val result = service.deleteCustomCategory(current, name)
        result.state?.let { updateState(it) }
        return result.status
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
        startSyncAutoRefreshIfConfigured(localState.syncFolderUri)

        viewModelScope.launch {
            val synced = service.loadSyncState(uri)
            if (synced != null) {
                val merged = synced.copy(
                    syncFolderUri = uri,
                    appSettings = _state.value.appSettings
                )
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
        stopSyncAutoRefresh()
    }

    fun updateAppSettings(settings: AppSettings) {
        val current = _state.value
        updateState(current.copy(appSettings = settings), writeSyncFile = false)
    }

    fun exportStateJson(): String {
        return json.encodeToString(TrackerState.serializer(), _state.value)
    }

    fun importStateJson(raw: String): Boolean {
        val parsed = runCatching {
            json.decodeFromString(TrackerState.serializer(), raw)
        }.getOrNull() ?: return false

        val normalized = parsed.copy(
            syncFolderUri = _state.value.syncFolderUri,
            appSettings = _state.value.appSettings
        )
        updateState(normalized, writeSyncFile = true)
        return true
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

    private fun startSyncAutoRefreshIfConfigured(syncFolderUri: String?) {
        val uri = syncFolderUri?.trim().orEmpty()
        if (uri.isBlank()) {
            stopSyncAutoRefresh()
            return
        }
        if (syncAutoRefreshJob?.isActive == true && syncAutoRefreshUri == uri) return
        stopSyncAutoRefresh()
        syncAutoRefreshUri = uri

        syncAutoRefreshJob = viewModelScope.launch {
            while (isActive) {
                runCatching {
                    val synced = service.loadSyncState(uri) ?: return@runCatching
                    val merged = synced.copy(
                        syncFolderUri = uri,
                        appSettings = _state.value.appSettings
                    )
                    val current = _state.value
                    val hasChanged = withContext(Dispatchers.Default) {
                        merged != current
                    }
                    if (hasChanged) {
                        _state.value = merged
                        service.persistLocalState(merged)
                    }
                }
                delay(syncAutoRefreshIntervalMs)
            }
        }
    }

    private fun stopSyncAutoRefresh() {
        syncAutoRefreshJob?.cancel()
        syncAutoRefreshJob = null
        syncAutoRefreshUri = null
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val service: TrackerService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(service) as T
        }
    }
}
