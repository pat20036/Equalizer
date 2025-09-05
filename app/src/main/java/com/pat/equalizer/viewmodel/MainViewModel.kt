package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.bassboost.core.BassBoostController
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.viewmodel.extensions.BaseUiState
import com.pat.equalizer.viewmodel.extensions.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val equalizerController: EqualizerController,
    private val bassBoostController: BassBoostController
) :
    StateViewModel<MainUiState, MainAction>() {

    override val state: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())

    init {
        viewModelScope.launch {
            equalizerController.configuration.collectLatest {
                updateState(state.value.copy(equalizer = state.value.equalizer.copy(presets = it.presets, switchState = it.enabled)))
            }
        }

        viewModelScope.launch {
            bassBoostController.configuration.collectLatest {
                updateState(state.value.copy(bassBoost = state.value.bassBoost.copy(strength = it.strength, switchState = it.enabled)))
            }
        }

        collectLatestAction {
            when (it) {
                is MainAction.UsePreset -> {
                    viewModelScope.launch {
                        equalizerController.usePreset(it.preset)
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
            }
        }
    }
}

data class MainUiState(
    val equalizer: EqualizerUiState = EqualizerUiState(),
    val bassBoost: BassBoostUiState = BassBoostUiState(),
) : BaseUiState

data class EqualizerUiState(
    val presets: List<Preset> = emptyList(),
    val switchState: Boolean = false
) : BaseUiState

data class BassBoostUiState(
    val strength: Int = 0,
    val switchState: Boolean = false,
    val range: IntRange = 0..1000 // According to Android docs, strength is between 0 and 1000
) : BaseUiState

sealed interface MainAction {
    data class UsePreset(val preset: Preset) : MainAction
    data class OnBandLevelChanged(val preset: Preset, val bandId: Int, val level: Short) : MainAction
    data class SetEqualizerSwitchState(val isChecked: Boolean) : MainAction
    data class SetBassBoostSwitchState(val isChecked: Boolean) : MainAction
    data class SetBassBoostStrength(val strength: Int) : MainAction
}