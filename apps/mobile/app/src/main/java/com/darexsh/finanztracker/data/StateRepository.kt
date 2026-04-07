package com.darexsh.finanztracker.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.darexsh.finanztracker.model.Booking
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TrackerUser
import com.darexsh.finanztracker.model.TxType
import com.darexsh.finanztracker.model.defaultState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
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
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val syncFileName = "finanz-tracker-sync-latest.json"

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

            val users = root["users"]?.jsonArray
                ?.mapNotNull { userEl ->
                    val obj = userEl.jsonObject
                    val id = obj.stringOrNull("id") ?: return@mapNotNull null
                    val name = obj.stringOrNull("name") ?: return@mapNotNull null
                    TrackerUser(id = id, name = name)
                }
                .orEmpty()

            val safeUsers = if (users.isEmpty()) {
                listOf(TrackerUser(id = "default", name = "Standard"))
            } else {
                users
            }

            val activeUserIdFromFile = root.stringOrNull("activeUserId")
            val activeUserId = if (activeUserIdFromFile != null && safeUsers.any { it.id == activeUserIdFromFile }) {
                activeUserIdFromFile
            } else {
                safeUsers.first().id
            }

            val customCategories = root["customCategories"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.contentOrNull }
                .orEmpty()

            val baseTime = System.currentTimeMillis()
            val bookings = root["bookings"]?.jsonArray
                ?.mapIndexedNotNull { index, bookingEl ->
                    val obj = bookingEl.jsonObject
                    val txType = parseTxType(obj["txType"])
                    val userId = obj.stringOrNull("userId") ?: activeUserId
                    val monthOrDate = obj.stringOrNull("date") ?: obj.stringOrNull("month")
                    val date = normalizeDate(monthOrDate ?: return@mapIndexedNotNull null)

                    Booking(
                        id = obj.stringOrNull("id") ?: "b_sync_${baseTime + index}",
                        userId = userId,
                        date = date,
                        description = obj.stringOrNull("description").orEmpty(),
                        category = obj.stringOrNull("category").orEmpty(),
                        txType = txType,
                        amount = obj.doubleOrNull("amount") ?: 0.0,
                        account = obj.stringOrNull("account").orEmpty(),
                        note = obj.stringOrNull("note").orEmpty(),
                        taxDeclaration = obj.booleanOrNull("taxDeclaration") ?: false,
                        createdAt = obj.longOrNull("createdAt") ?: (baseTime + index)
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
            put("users", JsonArray(state.users.map { user ->
                buildJsonObject {
                    put("id", JsonPrimitive(user.id))
                    put("name", JsonPrimitive(user.name))
                }
            }))

            put("activeUserId", JsonPrimitive(state.activeUserId))

            put("bookings", buildJsonArray {
                state.bookings.forEach { booking ->
                    add(buildJsonObject {
                        put("id", JsonPrimitive(booking.id))
                        put("userId", JsonPrimitive(booking.userId))
                        put("month", JsonPrimitive(booking.date))
                        put("description", JsonPrimitive(booking.description))
                        put("category", JsonPrimitive(booking.category))
                        put("txType", JsonPrimitive(toSyncTxType(booking.txType)))
                        put("amount", JsonPrimitive(booking.amount))
                        put("account", JsonPrimitive(booking.account))
                        put("note", JsonPrimitive(booking.note))
                        put("taxDeclaration", JsonPrimitive(booking.taxDeclaration))
                        put("createdAt", JsonPrimitive(booking.createdAt))
                    })
                }
            })

            put("customCategories", buildJsonArray {
                state.customCategories.forEach { add(JsonPrimitive(it)) }
            })
        }

        return json.encodeToString(JsonObject.serializer(), root)
    }

    private fun toSyncTxType(txType: TxType): String {
        return if (txType == TxType.INCOME) "Einnahme" else "Ausgabe"
    }

    private fun parseTxType(value: JsonElement?): TxType {
        val raw = value?.jsonPrimitive?.contentOrNull?.trim()?.lowercase() ?: return TxType.EXPENSE
        return when (raw) {
            "income", "einnahme" -> TxType.INCOME
            else -> TxType.EXPENSE
        }
    }

    private fun normalizeDate(raw: String): String {
        val text = raw.trim()

        val full = Regex("^\\d{2}\\.\\d{2}\\.\\d{4}$")
        if (full.matches(text)) return text

        val short = Regex("^\\d{2}\\.\\d{4}$")
        if (short.matches(text)) return "01.$text"

        val flexible = Regex("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})$")
        val match = flexible.find(text)
        if (match != null) {
            val day = match.groupValues[1].padStart(2, '0')
            val month = match.groupValues[2].padStart(2, '0')
            val year = match.groupValues[3]
            return "$day.$month.$year"
        }

        return text
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
