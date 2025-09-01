package com.pat.equalizer.repository

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.Equalizer
import androidx.datastore.core.DataStore
import com.google.gson.Gson
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.CustomPreset
import com.pat.equalizer.model.Preset
import java.util.prefs.Preferences
import javax.inject.Inject
import androidx.core.content.edit

interface EqualizerController {

    fun getPresets(): List<Preset>

    fun usePreset(preset: Short)

    fun setBandLevel(band: Short, level: Short)

    fun getBandsLevel(): List<BandLevel>

    fun useCustomPreset()

    fun saveCustomPreset()

    fun getCustomPreset(): CustomPreset
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

    override fun useCustomPreset() {
        useCustomPreset(getCustomPreset(context))
    }

    override fun saveCustomPreset() {
        setCustomPreset(context)
    }

    override fun getCustomPreset(): CustomPreset {
        return getCustomPreset(context)
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


    private fun setCustomPreset(context: Context) {
        val equalizerPreferences = context.getSharedPreferences(EQUALIZER_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        equalizerPreferences.edit {
            putString(
                EQUALIZER_CUSTOM_PRESET_PREFERENCE, Gson().toJson(
                    CustomPreset(
                        bandLevel0 = equalizer.getBandLevel(0),
                        bandLevel1 = equalizer.getBandLevel(1),
                        bandLevel2 = equalizer.getBandLevel(2),
                        bandLevel3 = equalizer.getBandLevel(3),
                        bandLevel4 = equalizer.getBandLevel(4)
                    )
                )
            )
        }
    }

    private fun getCustomPreset(context: Context): CustomPreset {
        val equalizerPreferences = context.getSharedPreferences(EQUALIZER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val customPresetPreferences = equalizerPreferences.getString(EQUALIZER_CUSTOM_PRESET_PREFERENCE, null)

        return customPresetPreferences?.let {
            Gson().fromJson(it, CustomPreset::class.java) } ?: CustomPreset()
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
        private const val EQUALIZER_SHARED_PREFERENCES = "EQUALIZER"
        private const val EQUALIZER_CUSTOM_PRESET_PREFERENCE = "CUSTOM_PRESET"
    }
}