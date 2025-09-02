package com.pat.equalizer.repository

import android.content.Context
import android.media.audiofx.Equalizer
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.Preset
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface EqualizerController {
    fun getPresets(): List<Preset>
    fun usePreset(preset: Preset, bandLevels: (List<BandLevel>) -> Unit)
}

class EqualizerControllerImpl @Inject constructor(private val context: Context, private val equalizer: Equalizer) : EqualizerController {

    init {
        equalizer.apply {
            if (!enabled) enabled = true
        }
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