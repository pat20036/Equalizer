package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.bassboost.core.BassBoostController
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.utils.Constants.VALUE_MAX_RANGE
import com.pat.equalizer.utils.Constants.VALUE_MIN_RANGE
import com.pat.equalizer.viewmodel.extensions.BaseUiState
import com.pat.equalizer.viewmodel.extensions.StateViewModel
import com.pat.equalizer.virtualizer.core.VirtualizerController
import com.pat.equalizer.volume.core.VolumeController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val equalizerController: EqualizerController,
    private val bassBoostController: BassBoostController,
    private val virtualizerController: VirtualizerController,
    volumeController: VolumeController
) :
    StateViewModel<MainUiState, MainAction>() {

    override val state: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())

    init {

        updateState(state.value.copy(volume = state.value.volume.copy(currentLevel = volumeController.getCurrentVolumeLevel(), maxLevel = volumeController.getMaxVolumeLevel())))

        viewModelScope.launch {
            equalizerController.configuration.collectLatest {
                updateState(
                    state.value.copy(
                        equalizer = state.value.equalizer.copy(
                            presets = it.presets,
                            loudnessEnhancerCheckboxState = it.loudnessEnhancerEnabled,
                            switchState = it.enabled
                        )
                    )
                )
            }
        }

        viewModelScope.launch {
            bassBoostController.configuration.collectLatest {
                updateState(state.value.copy(bassBoost = state.value.bassBoost.copy(strength = it.strength, switchState = it.enabled)))
            }
        }

        viewModelScope.launch {
            virtualizerController.configuration.collectLatest {
                updateState(state.value.copy(virtualizer = state.value.virtualizer.copy(strength = it.strength, switchState = it.enabled)))
            }
        }

        collectLatestAction {
            when (it) {
                is MainAction.UsePreset -> {
                    viewModelScope.launch {
                        equalizerController.usePreset(it.preset)
                    }
                }

                is MainAction.DeletePreset -> {
                    viewModelScope.launch {
                        equalizerController.deletePreset(it.preset)
                    }
                }

                is MainAction.OnBandLevelChanged -> {
                    viewModelScope.launch {
                        equalizerController.onBandLevelChanged(it.preset, it.bandId, it.level)
                    }
                }

                is MainAction.SetEqualizerSwitchState -> {
                    viewModelScope.launch {
                        equalizerController.changeState(it.isChecked)
                    }
                }

                is MainAction.SetBassBoostSwitchState -> {
                    viewModelScope.launch {
                        bassBoostController.changeState(it.isChecked)
                    }
                }

                is MainAction.SetBassBoostStrength -> {
                    viewModelScope.launch {
                        bassBoostController.setStrength(it.strength)
                    }
                }

                is MainAction.SetVirtualizerSwitchState -> {
                    viewModelScope.launch {
                        virtualizerController.changeState(it.isChecked)
                    }
                }

                is MainAction.SetVirtualizerStrength -> {
                    viewModelScope.launch {
                        virtualizerController.setStrength(it.strength)
                    }
                }

                is MainAction.SetVolumeLevel -> {
                    updateState(state.value.copy(volume = state.value.volume.copy(currentLevel = it.level)))
                }

                is MainAction.SetEnhanceLoudness -> viewModelScope.launch {
                    equalizerController.changeLoudnessEnhancerState(it.enabled)
                }
            }
        }
    }
}

data class MainUiState(
    val equalizer: EqualizerUiState = EqualizerUiState(),
    val bassBoost: BassBoostUiState = BassBoostUiState(),
    val virtualizer: VirtualizerUiState = VirtualizerUiState(),
    val volume: VolumeUiState = VolumeUiState()
) : BaseUiState

data class EqualizerUiState(
    val presets: List<Preset> = emptyList(),
    val switchState: Boolean = false,
    val loudnessEnhancerCheckboxState: Boolean = false
) : BaseUiState

data class BassBoostUiState(
    val strength: Int = 0,
    val switchState: Boolean = false,
    val range: IntRange = VALUE_MIN_RANGE..VALUE_MAX_RANGE
) : BaseUiState

data class VirtualizerUiState(
    val strength: Int = 0,
    val switchState: Boolean = false,
    val range: IntRange = VALUE_MIN_RANGE..VALUE_MAX_RANGE
) : BaseUiState

data class VolumeUiState(
    val currentLevel: Int = 0,
    val maxLevel: Int = 0
) : BaseUiState

sealed interface MainAction {
    data class UsePreset(val preset: Preset) : MainAction
    data class DeletePreset(val preset: Preset) : MainAction
    data class OnBandLevelChanged(val preset: Preset, val bandId: Int, val level: Short) : MainAction
    data class SetEqualizerSwitchState(val isChecked: Boolean) : MainAction
    data class SetBassBoostSwitchState(val isChecked: Boolean) : MainAction
    data class SetBassBoostStrength(val strength: Int) : MainAction
    data class SetVirtualizerSwitchState(val isChecked: Boolean) : MainAction
    data class SetVirtualizerStrength(val strength: Int) : MainAction
    data class SetVolumeLevel(val level: Int) : MainAction
    data class SetEnhanceLoudness(val enabled: Boolean) : MainAction
}