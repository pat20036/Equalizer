package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
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
class EqualizerViewModel @Inject constructor(private val equalizerController: EqualizerController) : StateViewModel<EqualizerUiState, EqualizerAction>() {

    override val state: MutableStateFlow<EqualizerUiState> = MutableStateFlow(EqualizerUiState())

    init {
        viewModelScope.launch {
            equalizerController.configuration.collectLatest {
                updateState(state.value.copy(presets = it.presets, equalizerSwitchState = it.enabled))
            }
        }

        collectLatestAction {
            when (it) {
                is EqualizerAction.UsePreset -> {
                    viewModelScope.launch {
                        equalizerController.usePreset(it.preset)
                    }
                }

                is EqualizerAction.AddCustomPreset -> {
                    viewModelScope.launch {
                        equalizerController.addCustomPreset(it.name)
                    }
                }

                is EqualizerAction.OnBandLevelChanged -> {
                    viewModelScope.launch {
                        equalizerController.onBandLevelChanged(it.preset, it.bandId, it.level)
                    }
                }

                is EqualizerAction.SetSwitchState -> {
                    viewModelScope.launch {
                        equalizerController.changeState(it.isChecked)
                    }
                }
            }
        }
    }
}

data class EqualizerUiState(
    val presets: List<Preset> = emptyList(),
    val equalizerSwitchState: Boolean = false
) : BaseUiState

sealed interface EqualizerAction {
    data class UsePreset(val preset: Preset) : EqualizerAction
    data class AddCustomPreset(val name: String) : EqualizerAction
    data class OnBandLevelChanged(val preset: Preset, val bandId: Int, val level: Short) : EqualizerAction
    data class SetSwitchState(val isChecked: Boolean) : EqualizerAction
}