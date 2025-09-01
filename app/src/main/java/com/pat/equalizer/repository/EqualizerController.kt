package com.pat.equalizer.repository

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.Equalizer
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.Preset
import javax.inject.Inject

interface EqualizerController {

    fun getPresets(): List<Preset>

    fun usePreset(preset: Short)

    fun setBandLevel(band: Short, level: Short)

    fun getBandsLevel(): List<BandLevel>
}

class EqualizerControllerImpl @Inject constructor(private val context: Context) : EqualizerController {

    private var equalizer: Equalizer = Equalizer(AudioManager.STREAM_MUSIC, 0).apply {
        if (!enabled) enabled = true
    }

    override fun setBandLevel(band: Short, level: Short) {
        equalizer.setBandLevel(band, level)
    }

    override fun getBandsLevel(): List<BandLevel> {
        val levels = mutableListOf<BandLevel>()
        for (level in 0 until equalizer.numberOfBands) {
            val levelIndex = level.toShort()
            levels.add(
                BandLevel(
                    level = equalizer.getBandLevel(levelIndex).toFloat(),
                    hzCenterFrequency = equalizer.getCenterFreq(levelIndex).convertMiliherzToHerzFormatted()
                )
            )
        }
        return levels
    }

    override fun getPresets(): List<Preset> {
        val presets = mutableListOf<Preset>()

        for (preset in 0 until equalizer.numberOfPresets) {
            val presetIndex = preset.toShort()
            presets.add(Preset(name = equalizer.getPresetName(presetIndex), id = presetIndex))
        }
        return presets
    }

    override fun usePreset(preset: Short) {
        equalizer.usePreset(preset)
    }

    private fun Int.convertMiliherzToHerzFormatted() = (this / 1000).toString() + " Hz"
}