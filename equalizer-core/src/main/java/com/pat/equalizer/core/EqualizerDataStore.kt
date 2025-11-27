package com.pat.equalizer.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.pat.equalizer.core.model.Preset
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
    private val enabledKey = booleanPreferencesKey("enabled")
    private val loudnessEnhancerEnabled = booleanPreferencesKey("loudnessEnhancerEnabled")

    fun getPresets(): Flow<List<Preset>> =
        context.dataStore.data.map { preferences ->
            preferences[presetsKey]?.let { json ->
                gson.fromJson(json, Array<Preset>::class.java)?.toList() ?: emptyList()
            } ?: emptyList()
        }

    fun getConfiguration(): Flow<EqualizerConfiguration> =
        context.dataStore.data.map { preferences ->
            val enabled = preferences[enabledKey] ?: false
            val loudnessEnhancerEnabled = preferences[loudnessEnhancerEnabled] ?: false
            val presets = preferences[presetsKey]?.let { json ->
                gson.fromJson(json, Array<Preset>::class.java)?.toList() ?: emptyList()
            } ?: emptyList()
            EqualizerConfiguration(equalizerEnabled = enabled, loudnessEnhancerEnabled = loudnessEnhancerEnabled, presets = presets)
        }

    suspend fun updateConfiguration(config: EqualizerConfiguration) {
        val json = gson.toJson(config.presets)
        context.dataStore.edit { prefs ->
            prefs[enabledKey] = config.equalizerEnabled
            prefs[presetsKey] = json
            prefs[loudnessEnhancerEnabled] = config.loudnessEnhancerEnabled
        }
    }

    suspend fun addPreset(preset: Preset) {
        val currentPresets = getPresets().first()
        val updatedPresets = currentPresets.toMutableList().apply { add(preset) }
        val json = gson.toJson(updatedPresets)
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = json
        }
    }

    suspend fun updatePreset(preset: Preset) {
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

    suspend fun deletePreset(preset: Preset) {
        val currentPresets = getPresets().first()
        val updatedPresets = currentPresets.toMutableList().apply { remove(preset) }
        val json = gson.toJson(updatedPresets)
        context.dataStore.edit { prefs ->
            prefs[presetsKey] = json
        }
    }
}

data class EqualizerConfiguration(
    var equalizerEnabled: Boolean = false,
    var loudnessEnhancerEnabled: Boolean = false,
    var presets: List<Preset> = emptyList()
)