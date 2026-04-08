package com.darexsh.finanztracker.domain

import com.darexsh.finanztracker.data.StateRepository
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

interface TrackerService {
    fun loadState(): TrackerState
    fun addUser(state: TrackerState, name: String): TrackerState?
    fun renameActiveUser(state: TrackerState, newName: String): TrackerState?
    fun deleteActiveUser(state: TrackerState): TrackerState?
    fun addBooking(state: TrackerState, draft: BookingDraft): TrackerState?
    fun updateBooking(state: TrackerState, bookingId: String, draft: BookingDraft): TrackerState?
    fun deleteBooking(state: TrackerState, bookingId: String): TrackerState?
    fun deleteBookings(state: TrackerState, bookingIds: Set<String>): TrackerState?
    fun setBookingTaxDeclaration(state: TrackerState, bookingId: String, taxDeclaration: Boolean): TrackerState
    fun setActiveUser(state: TrackerState, userId: String): TrackerState?
    fun setSyncFolderUri(state: TrackerState, uri: String): TrackerState
    fun clearSyncFolderUri(state: TrackerState): TrackerState
    suspend fun loadSyncState(syncFolderUri: String): TrackerState?
    suspend fun autoLoadFromSyncOnStart(state: TrackerState): TrackerState?
    suspend fun persistState(state: TrackerState, writeSyncFile: Boolean = true)
    suspend fun persistLocalState(state: TrackerState)
}

class DefaultTrackerService(
    private val repository: StateRepository
) : TrackerService {
    private fun todayString(): String = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(Date())

    override fun loadState(): TrackerState = repository.loadState()

    override fun addUser(state: TrackerState, name: String): TrackerState? {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return null
        if (state.users.any { it.name.equals(trimmed, ignoreCase = true) }) return null

        val newUser = com.darexsh.finanztracker.model.TrackerUser(
            id = "u_" + Random.nextLong().toString(),
            name = trimmed
        )
        return state.copy(
            users = state.users + newUser,
            activeUserId = newUser.id
        )
    }

    override fun renameActiveUser(state: TrackerState, newName: String): TrackerState? {
        val active = state.users.firstOrNull { it.id == state.activeUserId } ?: return null
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return null
        if (state.users.any { it.id != active.id && it.name.equals(trimmed, ignoreCase = true) }) return null

        return state.copy(
            users = state.users.map { user ->
                if (user.id == active.id) user.copy(name = trimmed) else user
            }
        )
    }

    override fun deleteActiveUser(state: TrackerState): TrackerState? {
        if (state.users.size <= 1) return null
        val active = state.users.firstOrNull { it.id == state.activeUserId } ?: return null
        val remainingUsers = state.users.filterNot { it.id == active.id }
        val nextActiveId = remainingUsers.firstOrNull()?.id ?: return null
        return state.copy(
            users = remainingUsers,
            activeUserId = nextActiveId,
            bookings = state.bookings.filterNot { it.userId == active.id }
        )
    }

    override fun addBooking(state: TrackerState, draft: BookingDraft): TrackerState? {
        val activeUser = state.users.firstOrNull { it.id == state.activeUserId } ?: return null
        val booking = Booking(
            id = "b_" + Random.nextLong().toString(),
            userId = activeUser.id,
            date = draft.date.trim().ifBlank { todayString() },
            description = draft.description.trim(),
            category = FinanceCatalog.normalizeCategory(draft.category),
            txType = draft.txType,
            amount = draft.amount,
            account = draft.account.trim().ifBlank { "Checking Account" },
            note = draft.note,
            taxDeclaration = draft.taxDeclaration,
            createdAt = System.currentTimeMillis()
        )
        return state.copy(bookings = listOf(booking) + state.bookings)
    }

    override fun updateBooking(state: TrackerState, bookingId: String, draft: BookingDraft): TrackerState? {
        val existing = state.bookings.firstOrNull { it.id == bookingId } ?: return null
        val updated = existing.copy(
            date = draft.date.trim().ifBlank { existing.date },
            description = draft.description.trim(),
            amount = draft.amount,
            category = FinanceCatalog.normalizeCategory(draft.category),
            txType = draft.txType,
            account = draft.account.trim().ifBlank { "Checking Account" },
            note = draft.note,
            taxDeclaration = draft.taxDeclaration
        )
        return state.copy(
            bookings = state.bookings.map { booking ->
                if (booking.id == bookingId) updated else booking
            }
        )
    }

    override fun deleteBooking(state: TrackerState, bookingId: String): TrackerState? {
        val newBookings = state.bookings.filterNot { it.id == bookingId }
        if (newBookings.size == state.bookings.size) return null
        return state.copy(bookings = newBookings)
    }

    override fun deleteBookings(state: TrackerState, bookingIds: Set<String>): TrackerState? {
        if (bookingIds.isEmpty()) return null
        val newBookings = state.bookings.filterNot { bookingIds.contains(it.id) }
        if (newBookings.size == state.bookings.size) return null
        return state.copy(bookings = newBookings)
    }

    override fun setBookingTaxDeclaration(
        state: TrackerState,
        bookingId: String,
        taxDeclaration: Boolean
    ): TrackerState {
        return state.copy(
            bookings = state.bookings.map { booking ->
                if (booking.id == bookingId) {
                    booking.copy(taxDeclaration = taxDeclaration)
                } else {
                    booking
                }
            }
        )
    }

    override fun setActiveUser(state: TrackerState, userId: String): TrackerState? {
        if (state.users.none { it.id == userId }) return null
        return state.copy(activeUserId = userId)
    }

    override fun setSyncFolderUri(state: TrackerState, uri: String): TrackerState {
        return state.copy(syncFolderUri = uri.trim())
    }

    override fun clearSyncFolderUri(state: TrackerState): TrackerState {
        return state.copy(syncFolderUri = null)
    }

    override suspend fun loadSyncState(syncFolderUri: String): TrackerState? = withContext(Dispatchers.IO) {
        repository.loadSyncState(syncFolderUri)
    }

    override suspend fun autoLoadFromSyncOnStart(state: TrackerState): TrackerState? {
        val syncUri = state.syncFolderUri ?: return null
        val synced = loadSyncState(syncUri) ?: return null
        return synced.copy(syncFolderUri = syncUri)
    }

    override suspend fun persistState(state: TrackerState, writeSyncFile: Boolean) = withContext(Dispatchers.IO) {
        repository.saveState(state)
        val syncUri = state.syncFolderUri
        if (writeSyncFile && !syncUri.isNullOrBlank()) {
            repository.saveSyncState(syncUri, state)
        }
    }

    override suspend fun persistLocalState(state: TrackerState) = withContext(Dispatchers.IO) {
        repository.saveState(state)
    }
}
