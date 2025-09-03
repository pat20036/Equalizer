package com.pat.equalizer.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.pat.equalizer.model.Preset
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "equalizerPreferences")

@Singleton
class EqualizerDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val presetsKey = stringPreferencesKey("presetsJson")

    fun getPresets(): Flow<List<Preset>> =
        context.dataStore.data.map { preferences ->
            preferences[presetsKey]?.let { json ->
                gson.fromJson(json, Array<Preset>::class.java)?.toList() ?: emptyList()
            } ?: emptyList()
        }

    suspend fun addPreset(preset: Preset) {
        val currentPresets = getPresets().first()
        val updatedPresets = currentPresets.toMutableList().apply { add(preset) }
        val json = gson.toJson(updatedPresets)
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = json
        }
    }

    suspend fun updateSinglePreset(preset: Preset) {
        val currentPresets = getPresets().first()
        val updatedPresets = currentPresets.map { if (it.id == preset.id) preset else it }
        val json = gson.toJson(updatedPresets)
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = json
        }
    }

    suspend fun updateAllPresets(presets: List<Preset>) {
        val json = gson.toJson(presets)
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = json
        }
    }
}