package com.pat.equalizer.repository

import android.content.Context
import android.media.audiofx.Equalizer
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.CustomPreset
import com.pat.equalizer.model.Preset
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = EqualizerControllerImpl.EQUALIZER_DATA_STORE)

interface EqualizerController {
    fun getPresets(): List<Preset>
    fun usePreset(preset: Preset, bandLevels: (List<BandLevel>) -> Unit)
    fun setBandLevel(band: Short, level: Short)
    suspend fun useCustomPreset()
    suspend fun saveCustomPreset()
    suspend fun getCustomPreset(): CustomPreset
}

class EqualizerControllerImpl @Inject constructor(private val context: Context, private val equalizer: Equalizer) : EqualizerController {
    private val customPresetKey = stringPreferencesKey(EQUALIZER_CUSTOM_PRESET_PREFERENCE)

    init {
        equalizer.apply {
            if (!enabled) enabled = true
        }
    }

    override fun setBandLevel(band: Short, level: Short) {
        equalizer.setBandLevel(band, level)
    }

    override suspend fun useCustomPreset() {
        useCustomPreset(getCustomPreset())
    }

    override suspend fun saveCustomPreset() {
        setCustomPreset()
    }

    override suspend fun getCustomPreset(): CustomPreset {
        val preferences = context.dataStore.data.first()
        val customPresetPreferences = preferences[customPresetKey]
        return customPresetPreferences?.let {
            Gson().fromJson(it, CustomPreset::class.java)
        } ?: CustomPreset()
    }

    override fun getPresets(): List<Preset> {
        return getSystemPresets()
    }

    override fun usePreset(preset: Preset, bandLevels: (List<BandLevel>) -> Unit) {
        equalizer.usePreset(preset.id)
        bandLevels(getSystemPresetsBandLevels())
    }

    private fun getSystemPresets(): List<Preset> {
        val presets = mutableListOf<Preset>()

        for (preset in 0 until equalizer.numberOfPresets) {
            val presetIndex = preset.toShort()
            presets.add(
                Preset(
                    name = equalizer.getPresetName(presetIndex),
                    id = presetIndex,
                    bandLevels = getSystemPresetsBandLevels(),
                    selected = presetIndex == 0.toShort()
                )
            )
        }

        return presets
    }

    private fun getSystemPresetsBandLevels(): List<BandLevel> {
        val levels = mutableListOf<BandLevel>()
        for (level in 0 until equalizer.numberOfBands) {
            val levelIndex = level.toShort()
            levels.add(
                BandLevel(
                    level = equalizer.getBandLevel(levelIndex),
                    hzCenterFrequency = equalizer.getCenterFreq(levelIndex).convertMiliherzToHerzFormatted()
                )
            )
        }
        return levels
    }

    private suspend fun setCustomPreset() {
        val customPreset = CustomPreset(
            bandLevel0 = equalizer.getBandLevel(0),
            bandLevel1 = equalizer.getBandLevel(1),
            bandLevel2 = equalizer.getBandLevel(2),
            bandLevel3 = equalizer.getBandLevel(3),
            bandLevel4 = equalizer.getBandLevel(4)
        )
        context.dataStore.edit { preferences ->
            preferences[customPresetKey] = Gson().toJson(customPreset)
        }
    }

    private fun useCustomPreset(customPreset: CustomPreset) {
        equalizer.apply {
            setBandLevel(0, customPreset.bandLevel0)
            setBandLevel(1, customPreset.bandLevel1)
            setBandLevel(2, customPreset.bandLevel2)
            setBandLevel(3, customPreset.bandLevel3)
            setBandLevel(4, customPreset.bandLevel4)
        }
    }

    private fun Int.convertMiliherzToHerzFormatted() = (this / 1000).toString() + " Hz"

    companion object {
        const val EQUALIZER_DATA_STORE = "EQUALIZER"
        private const val EQUALIZER_CUSTOM_PRESET_PREFERENCE = "CUSTOM_PRESET"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class EqualizerControllerModule {
    @Binds
    @Singleton
    abstract fun bindEqualizerController(
        impl: EqualizerControllerImpl
    ): EqualizerController
}