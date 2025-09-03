package com.pat.equalizer.repository

import android.media.audiofx.Equalizer
import com.pat.equalizer.model.Band
import com.pat.equalizer.model.Preset
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface EqualizerController {
    val presets: StateFlow<List<Preset>>
    suspend fun usePreset(preset: Preset)
    suspend fun addCustomPreset(name: String)
    suspend fun onBandLevelChanged(preset: Preset, bandId: Int, level: Short)
}

class EqualizerControllerImpl @Inject constructor(
    private val equalizer: Equalizer,
    private val dataStore: EqualizerDataStore
) : EqualizerController {

    private val _presets = MutableStateFlow<List<Preset>>(emptyList())
    override val presets: StateFlow<List<Preset>> = _presets.asStateFlow()

    init {
        var isFirstLaunch = true

        equalizer.apply {
            if (!enabled) enabled = true
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (dataStore.getPresets().first().isEmpty()) {
                getSystemPresets().forEachIndexed { index, preset ->
                    dataStore.addPreset(preset.copy(selected = index == 0))
                }
            }

            dataStore.getPresets().collectLatest { presets ->
                _presets.value = presets
                if (isFirstLaunch) {
                    isFirstLaunch = false
                    presets.firstOrNull { it.selected }?.let {
                        usePreset(it)
                    }
                }
            }
        }
    }

    override suspend fun usePreset(preset: Preset) {
        if (preset.isCustom) {
            preset.bands.forEachIndexed { index, band ->
                equalizer.setBandLevel(index.toShort(), band.level)
            }
        } else {
            equalizer.usePreset(preset.id.toShort())
        }
        _presets.value = _presets.value.map {
            it.copy(selected = it.id == preset.id, bands = if (it.isCustom) it.bands else getBands())
        }

        dataStore.updateAllPresets(_presets.value)
    }

    override suspend fun addCustomPreset(name: String) {
        val preset = Preset(
            name = name,
            id = presets.value.lastIndex + 1,
            bands = getBands(true),
            isCustom = true
        )

        dataStore.addPreset(preset)
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

        dataStore.updateSinglePreset(updatedPreset)
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
            hzCenterFrequency = equalizer.getCenterFreq(index.toShort()).convertMiliherzToHerzFormatted()
        )
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