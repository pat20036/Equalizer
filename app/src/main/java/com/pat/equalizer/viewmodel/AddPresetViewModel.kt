package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.viewmodel.extensions.BaseUiState
import com.pat.equalizer.viewmodel.extensions.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPresetViewModel @Inject constructor(private val equalizerController: EqualizerController) : StateViewModel<AddPresetUiState, AddPresetUiAction>() {

    override val state: MutableStateFlow<AddPresetUiState> = MutableStateFlow(AddPresetUiState())

    init {
        collectLatestAction {
            when (it) {
                is AddPresetUiAction.AddCustomPreset -> {
                    viewModelScope.launch {
                        equalizerController.addCustomPreset(it.name)
                        updateState(state.value.copy(added = true))
                    }
                }
            }
        }
    }
}

data class AddPresetUiState(
    val added: Boolean = false,
) : BaseUiState

sealed interface AddPresetUiAction {
    data class AddCustomPreset(val name: String) : AddPresetUiAction
}