package com.pat.equalizer.bassboost.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pat.equalizer.bassboost.core.model.BassBoostConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "bassBoostPreferences")

@Singleton
class BassBoostDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val strengthKey = intPreferencesKey("strength")
    private val enabledKey = booleanPreferencesKey("enabled")

    fun getConfiguration(): Flow<BassBoostConfiguration> =
        context.dataStore.data.map { preferences ->
            val strength = preferences[strengthKey]?: 0
            val enabled = preferences[enabledKey] ?: false
            BassBoostConfiguration(strength = strength, enabled = enabled)
        }

    suspend fun updateConfiguration(config: BassBoostConfiguration) {
        context.dataStore.edit { prefs ->
            prefs[strengthKey] = config.strength
            prefs[enabledKey] = config.enabled
        }
    }
}