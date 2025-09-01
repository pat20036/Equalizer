package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.CustomPreset
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
        updateState(EqualizerUiState(presets = equalizerController.getPresets(), levels = equalizerController.getBandsLevel()))
        collectLatestAction {
            when (it) {
                is EqualizerAction.UsePreset -> {
                    equalizerController.usePreset(it.id)
                    updateState(state.value.copy(levels = equalizerController.getBandsLevel()))
                }

                is EqualizerAction.ChangeBandLevel -> {
                    equalizerController.setBandLevel(it.band, it.level)
                }

                EqualizerAction.UseCustomPreset -> {
                    viewModelScope.launch {
                        equalizerController.useCustomPreset()
                        updateState(state.value.copy(levels = equalizerController.getBandsLevel(), customPreset = equalizerController.getCustomPreset()))
                    }
                }

                EqualizerAction.SetCustomPreset -> viewModelScope.launch {
                    equalizerController.saveCustomPreset()
                }
            }
        }
    }
}

data class EqualizerUiState(
    val presets: List<Preset> = emptyList(),
    val levels: List<BandLevel> = emptyList(),
    val customPreset: CustomPreset = CustomPreset()
) : BaseUiState

sealed interface EqualizerAction {
    data class UsePreset(val id: Short) : EqualizerAction
    data class ChangeBandLevel(val band: Short, val level: Short) : EqualizerAction
    data object UseCustomPreset : EqualizerAction
    data object SetCustomPreset : EqualizerAction
}