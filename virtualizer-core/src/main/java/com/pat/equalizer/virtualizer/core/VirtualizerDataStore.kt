package com.pat.equalizer.virtualizer.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pat.equalizer.virtualizer.core.model.VirtualizerConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "virtualizerPreferences")

@Singleton
class VirtualizerDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val strengthKey = intPreferencesKey("strength")
    private val enabledKey = booleanPreferencesKey("enabled")

    fun getConfiguration(): Flow<VirtualizerConfiguration> =
        context.dataStore.data.map { preferences ->
            val strength = preferences[strengthKey]?: 0
            val enabled = preferences[enabledKey] ?: false
            VirtualizerConfiguration(strength = strength, enabled = enabled)
        }

    suspend fun updateConfiguration(config: VirtualizerConfiguration) {
        context.dataStore.edit { prefs ->
            prefs[strengthKey] = config.strength
            prefs[enabledKey] = config.enabled
        }
    }
}