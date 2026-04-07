package com.darexsh.finanztracker.data

import android.content.Context
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.defaultState
import kotlinx.serialization.json.Json
import java.io.File

class StateRepository(context: Context) {
    private val file = File(context.filesDir, "state.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

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
}
