package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.model.Preset
import com.pat.equalizer.repository.EqualizerController
import com.pat.equalizer.viewmodel.extensions.BaseUiState
import com.pat.equalizer.viewmodel.extensions.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(private val equalizerController: EqualizerController) : StateViewModel<EqualizerUiState, EqualizerAction>() {

    override val state: MutableStateFlow<EqualizerUiState> = MutableStateFlow(EqualizerUiState())

    init {
        updateState(EqualizerUiState(presets = equalizerController.getPresets()))

        collectLatestAction {
            when (it) {
                is EqualizerAction.UsePreset -> {
                    equalizerController.usePreset(it.preset) { bandLevels ->
                        updateState(state.value.copy(presets = state.value.presets.map { preset ->
                            if (preset.id == it.preset.id) {
                                preset.copy(selected = true, bandLevels = bandLevels)
                            } else {
                                preset.copy(selected = false)
                            }
                        }))
                    }
                }

                is EqualizerAction.AddCustomPreset -> {
                    viewModelScope.launch {
                        equalizerController.addCustomPreset(it.name)
                        updateState(state.value.copy(presets = equalizerController.getPresets()))
                    }
                }
            }
        }
    }
}

data class EqualizerUiState(
    val presets: List<Preset> = emptyList()
) : BaseUiState

sealed interface EqualizerAction {
    data class UsePreset(val preset: Preset) : EqualizerAction
    data class AddCustomPreset(val name: String) : EqualizerAction
}