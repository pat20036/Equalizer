package com.pat.equalizer.core

import android.media.audiofx.Equalizer
import com.pat.equalizer.core.model.Band
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.loudness.core.LoudnessController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface EqualizerController {
    val configuration: StateFlow<EqualizerConfiguration>
    suspend fun changeState(enabled: Boolean)
    suspend fun usePreset(preset: Preset)
    suspend fun deletePreset(preset: Preset)
    suspend fun addCustomPreset(name: String, onSuccess: (() -> Unit), onFailure: (() -> Unit))
    suspend fun onBandLevelChanged(preset: Preset, bandId: Int, level: Short)
    suspend fun changeLoudnessEnhancerState(enabled: Boolean)
}

class EqualizerControllerImpl @Inject constructor(
    private val equalizer: Equalizer,
    private val dataStore: EqualizerDataStore,
    private val loudnessController: LoudnessController
) : EqualizerController {

    private val _configuration = MutableStateFlow(EqualizerConfiguration())
    override val configuration: StateFlow<EqualizerConfiguration> = _configuration.asStateFlow()

    init {
        var isFirstLaunch = true

        CoroutineScope(Dispatchers.IO).launch {
            if (dataStore.getConfiguration().first().presets.isEmpty()) {
                getSystemPresets().forEachIndexed { index, preset ->
                    dataStore.addPreset(preset.copy(selected = index == 0))
                }
            }

            _configuration.value = dataStore.getConfiguration().first()

            configuration.collectLatest {
                equalizer.enabled = it.enabled
                loudnessController.setEnabled(it.loudnessEnhancerEnabled)

                if (isFirstLaunch) {
                    isFirstLaunch = false
                    it.presets.firstOrNull { it.selected }?.let { preset ->
                        usePreset(preset)
                    }
                }
            }
        }
    }

    override suspend fun changeState(enabled: Boolean) {
        updateConfigurationState(enabled = enabled)
        dataStore.updateConfiguration(configuration.value)
    }

    override suspend fun usePreset(preset: Preset) {
        if (preset.isCustom) {
            preset.bands.forEachIndexed { index, band ->
                equalizer.setBandLevel(index.toShort(), band.level)
            }
        } else {
            equalizer.usePreset(preset.id.toShort())
        }

        updateConfigurationState(presets = _configuration.value.presets.map {
            it.copy(selected = it.id == preset.id, bands = if (it.isCustom) it.bands else getBands())
        })

        dataStore.updateAllPresets(configuration.value.presets)
    }

    override suspend fun deletePreset(preset: Preset) {
        updateConfigurationState(presets = configuration.value.presets - preset)
        usePreset(configuration.value.presets.first())
        dataStore.deletePreset(preset)
    }

    override suspend fun addCustomPreset(name: String, onSuccess: (() -> Unit), onFailure: (() -> Unit)) {
        if (name.isNotBlank()) {
            val preset = Preset(
                name = name,
                id = configuration.value.presets.lastIndex + 1,
                bands = getBands(true),
                isCustom = true
            )

            updateConfigurationState(presets = configuration.value.presets + preset)
            dataStore.addPreset(preset)
            onSuccess()
        } else {
            onFailure()
        }

    }

    override suspend fun onBandLevelChanged(preset: Preset, bandId: Int, level: Short) {
        equalizer.setBandLevel(bandId.toShort(), level)

        val updatedPreset = preset.copy(
            bands = preset.bands.mapIndexed { index, band ->
                if (index == bandId) {
                    band.copy(level = level)
                } else band
            }
        )

        updateConfigurationState(presets = configuration.value.presets.map {
            if (it.id == preset.id) updatedPreset else it
        })

        dataStore.updatePreset(updatedPreset)
    }

    override suspend fun changeLoudnessEnhancerState(enabled: Boolean) {
        loudnessController.setEnabled(enabled)
        updateConfigurationState(loudnessEnhancerEnabled = enabled)
        dataStore.updateConfiguration(configuration.value)
    }

    private fun getSystemPresets() = List(equalizer.numberOfPresets.toInt()) { index ->
        Preset(
            name = equalizer.getPresetName(index.toShort()),
            id = index,
            bands = getBands()
        )
    }

    private fun getBands(default: Boolean = false) = List(equalizer.numberOfBands.toInt()) { index ->
        Band(
            level = if (default) 0 else equalizer.getBandLevel(index.toShort()),
            range = IntRange(
                start = equalizer.bandLevelRange[0].toInt(),
                endInclusive = equalizer.bandLevelRange[1].toInt()
            ),
            hzCenterFrequency = equalizer.getCenterFreq(index.toShort()).convertMiliherzToHerzFormatted()
        )
    }

    private fun updateConfigurationState(
        enabled: Boolean = configuration.value.enabled,
        loudnessEnhancerEnabled: Boolean = configuration.value.loudnessEnhancerEnabled,
        presets: List<Preset> = configuration.value.presets
    ) {
        _configuration.value = configuration.value.copy(enabled = enabled, loudnessEnhancerEnabled = loudnessEnhancerEnabled, presets = presets)
    }

    private fun Int.convertMiliherzToHerzFormatted() = (this / 1000).toString() + " Hz"
}