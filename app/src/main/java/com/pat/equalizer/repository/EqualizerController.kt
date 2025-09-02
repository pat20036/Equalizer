package com.pat.equalizer.repository

import android.media.audiofx.Equalizer
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.Preset
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface EqualizerController {
    fun getPresets(): List<Preset>
    fun usePreset(preset: Preset, bandLevels: (List<BandLevel>) -> Unit)
    suspend fun addCustomPreset(name: String)
}

class EqualizerControllerImpl @Inject constructor(
    private val equalizer: Equalizer,
    private val dataStore: EqualizerDataStore
) : EqualizerController {

    private var allPresets = mutableListOf<Preset>()

    init {
        equalizer.apply {
            if (!enabled) enabled = true
        }

        allPresets.addAll(getSystemPresets())

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.getPresets().collectLatest { customPresets ->
                allPresets.removeAll { it.isCustom }
                allPresets.addAll(customPresets)
            }
        }
    }

    override fun getPresets(): List<Preset> = allPresets

    override fun usePreset(preset: Preset, bandLevels: (List<BandLevel>) -> Unit) {
        if (preset.isCustom) {
            preset.bandLevels.forEachIndexed { index, band ->
                equalizer.setBandLevel(index.toShort(), band.level)
            }
        } else {
            equalizer.usePreset(preset.id)
        }
        bandLevels(getBandLevels())
    }

    override suspend fun addCustomPreset(name: String) {
        val preset = Preset(
            name = name,
            id = (allPresets.lastIndex + 1).toShort(),
            bandLevels = getBandLevels(true),
            isCustom = true
        )

        dataStore.addPreset(preset)
    }

    private fun getSystemPresets(): List<Preset> {
        val presets = mutableListOf<Preset>()

        for (preset in 0 until equalizer.numberOfPresets) {
            val presetIndex = preset.toShort()
            presets.add(
                Preset(
                    name = equalizer.getPresetName(presetIndex),
                    id = presetIndex,
                    bandLevels = getBandLevels(),
                    selected = presetIndex == 0.toShort()
                )
            )
        }

        return presets
    }

    private fun getBandLevels(default: Boolean = false): List<BandLevel> {
        val levels = mutableListOf<BandLevel>()
        for (level in 0 until equalizer.numberOfBands) {
            val levelIndex = level.toShort()
            levels.add(
                BandLevel(
                    level = if (default) 0 else equalizer.getBandLevel(levelIndex),
                    hzCenterFrequency = equalizer.getCenterFreq(levelIndex).convertMiliherzToHerzFormatted()
                )
            )
        }
        return levels
    }

    private fun Int.convertMiliherzToHerzFormatted() = (this / 1000).toString() + " Hz"
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