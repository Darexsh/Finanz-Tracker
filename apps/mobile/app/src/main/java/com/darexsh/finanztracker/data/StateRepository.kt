package com.darexsh.finanztracker.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.darexsh.finanztracker.domain.SyncContract
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TrackerUser
import com.darexsh.finanztracker.model.defaultState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File

class StateRepository(private val context: Context) {
    private val file = File(context.filesDir, "state.json")
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true; prettyPrint = true }
    private val syncFileName = SyncContract.SYNC_FILE_NAME

    fun loadState(): TrackerState {
        if (!file.exists()) return defaultState()
        return runCatching {
            json.decodeFromString<TrackerState>(file.readText())
        }.getOrElse {
            defaultState()
        }
    }

    fun saveState(state: TrackerState) {
        file.writeText(json.encodeToString(TrackerState.serializer(), state))
    }

    fun loadSyncState(syncFolderUri: String): TrackerState? {
        val syncFile = findSyncFile(syncFolderUri) ?: return null
        val content = runCatching {
            context.contentResolver.openInputStream(syncFile.uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull() ?: return null

        if (content.isBlank()) return null
        return parseSyncState(content)
    }

    fun saveSyncState(syncFolderUri: String, state: TrackerState): Boolean {
        val syncFile = getOrCreateSyncFile(syncFolderUri) ?: return false
        val payload = toSyncJson(state)

        return runCatching {
            context.contentResolver.openOutputStream(syncFile.uri, "wt")?.bufferedWriter()?.use { writer ->
                writer.write(payload)
            }
            true
        }.getOrElse { false }
    }

    fun hasSyncFile(syncFolderUri: String): Boolean {
        return findSyncFile(syncFolderUri) != null
    }

    private fun parseSyncState(raw: String): TrackerState? {
        return runCatching {
            val root = json.parseToJsonElement(raw).jsonObject

            val users = root[SyncContract.Keys.USERS]?.jsonArray
                ?.mapNotNull { userEl ->
                    val obj = userEl.jsonObject
                    val id = obj.stringOrNull(SyncContract.Keys.USER_ID) ?: return@mapNotNull null
                    val name = obj.stringOrNull(SyncContract.Keys.USER_NAME) ?: return@mapNotNull null
                    TrackerUser(id = id, name = name)
                }
                .orEmpty()

            val safeUsers = if (users.isEmpty()) {
                listOf(TrackerUser(id = "default", name = "Standard"))
            } else {
                users
            }

            val activeUserIdFromFile = root.stringOrNull(SyncContract.Keys.ACTIVE_USER_ID)
            val activeUserId = if (activeUserIdFromFile != null && safeUsers.any { it.id == activeUserIdFromFile }) {
                activeUserIdFromFile
            } else {
                safeUsers.first().id
            }

            val customCategories = root[SyncContract.Keys.CUSTOM_CATEGORIES]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.contentOrNull }
                .orEmpty()

            val baseTime = System.currentTimeMillis()
            val bookings = root[SyncContract.Keys.BOOKINGS]?.jsonArray
                ?.mapIndexedNotNull { index, bookingEl ->
                    val obj = bookingEl.jsonObject
                    val txType = SyncContract.parseTxType(
                        obj[SyncContract.Keys.BOOKING_TX_TYPE]?.jsonPrimitive?.contentOrNull
                    )
                    val userId = obj.stringOrNull(SyncContract.Keys.BOOKING_USER_ID) ?: activeUserId
                    val monthOrDate = obj.stringOrNull(SyncContract.Keys.BOOKING_DATE)
                        ?: obj.stringOrNull(SyncContract.Keys.BOOKING_MONTH)
                    val date = SyncContract.normalizeDate(monthOrDate ?: return@mapIndexedNotNull null)

                    Booking(
                        id = obj.stringOrNull(SyncContract.Keys.BOOKING_ID) ?: "b_sync_${baseTime + index}",
                        userId = userId,
                        date = date,
                        description = obj.stringOrNull(SyncContract.Keys.BOOKING_DESCRIPTION).orEmpty(),
                        category = obj.stringOrNull(SyncContract.Keys.BOOKING_CATEGORY).orEmpty(),
                        txType = txType,
                        amount = obj.doubleOrNull(SyncContract.Keys.BOOKING_AMOUNT) ?: 0.0,
                        account = obj.stringOrNull(SyncContract.Keys.BOOKING_ACCOUNT).orEmpty(),
                        note = obj.stringOrNull(SyncContract.Keys.BOOKING_NOTE).orEmpty(),
                        taxDeclaration = obj.booleanOrNull(SyncContract.Keys.BOOKING_TAX_DECLARATION) ?: false,
                        createdAt = obj.longOrNull(SyncContract.Keys.BOOKING_CREATED_AT) ?: (baseTime + index)
                    )
                }
                .orEmpty()

            TrackerState(
                users = safeUsers,
                activeUserId = activeUserId,
                bookings = bookings,
                customCategories = customCategories,
                syncFolderUri = null
            )
        }.getOrNull()
    }

    private fun toSyncJson(state: TrackerState): String {
        val root = buildJsonObject {
            put(SyncContract.Keys.USERS, JsonArray(state.users.map { user ->
                buildJsonObject {
                    put(SyncContract.Keys.USER_ID, JsonPrimitive(user.id))
                    put(SyncContract.Keys.USER_NAME, JsonPrimitive(user.name))
                }
            }))

            put(SyncContract.Keys.ACTIVE_USER_ID, JsonPrimitive(state.activeUserId))

            put(SyncContract.Keys.BOOKINGS, buildJsonArray {
                state.bookings.forEach { booking ->
                    add(buildJsonObject {
                        put(SyncContract.Keys.BOOKING_ID, JsonPrimitive(booking.id))
                        put(SyncContract.Keys.BOOKING_USER_ID, JsonPrimitive(booking.userId))
                        put(SyncContract.Keys.BOOKING_MONTH, JsonPrimitive(booking.date))
                        put(SyncContract.Keys.BOOKING_DESCRIPTION, JsonPrimitive(booking.description))
                        put(SyncContract.Keys.BOOKING_CATEGORY, JsonPrimitive(booking.category))
                        put(SyncContract.Keys.BOOKING_TX_TYPE, JsonPrimitive(SyncContract.toSyncTxType(booking.txType)))
                        put(SyncContract.Keys.BOOKING_AMOUNT, JsonPrimitive(booking.amount))
                        put(SyncContract.Keys.BOOKING_ACCOUNT, JsonPrimitive(booking.account))
                        put(SyncContract.Keys.BOOKING_NOTE, JsonPrimitive(booking.note))
                        put(SyncContract.Keys.BOOKING_TAX_DECLARATION, JsonPrimitive(booking.taxDeclaration))
                        put(SyncContract.Keys.BOOKING_CREATED_AT, JsonPrimitive(booking.createdAt))
                    })
                }
            })

            put(SyncContract.Keys.CUSTOM_CATEGORIES, buildJsonArray {
                state.customCategories.forEach { add(JsonPrimitive(it)) }
            })
        }

        return json.encodeToString(JsonObject.serializer(), root)
    }

    private fun JsonObject.stringOrNull(key: String): String? {
        return this[key]?.jsonPrimitive?.contentOrNull
    }

    private fun JsonObject.doubleOrNull(key: String): Double? {
        return this[key]?.jsonPrimitive?.doubleOrNull
    }

    private fun JsonObject.longOrNull(key: String): Long? {
        return this[key]?.jsonPrimitive?.longOrNull
    }

    private fun JsonObject.booleanOrNull(key: String): Boolean? {
        return this[key]?.jsonPrimitive?.booleanOrNull
    }

    private fun findSyncFile(syncFolderUri: String): DocumentFile? {
        val folderUri = runCatching { Uri.parse(syncFolderUri) }.getOrNull() ?: return null
        val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return null
        if (!folder.isDirectory) return null

        val existing = folder.findFile(syncFileName)
        return if (existing != null && existing.isFile) existing else null
    }

    private fun getOrCreateSyncFile(syncFolderUri: String): DocumentFile? {
        val existing = findSyncFile(syncFolderUri)
        if (existing != null) return existing

        val folderUri = runCatching { Uri.parse(syncFolderUri) }.getOrNull() ?: return null
        val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return null
        if (!folder.isDirectory) return null

        return folder.createFile("application/json", syncFileName)
    }
}
