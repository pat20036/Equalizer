package com.pat.equalizer.viewmodel

import androidx.lifecycle.viewModelScope
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.viewmodel.extensions.BaseUiState
import com.pat.equalizer.viewmodel.extensions.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPresetViewModel @Inject constructor(private val equalizerController: EqualizerController) : StateViewModel<BaseUiState, AddPresetUiAction>() {

    override val state: MutableStateFlow<BaseUiState> = MutableStateFlow(object : BaseUiState {})

    val oneTimeEventsChannel = Channel<AddPresetUiEvent>()

    init {
        collectLatestAction {
            when (it) {
                is AddPresetUiAction.AddCustomPreset -> {
                    viewModelScope.launch {
                        equalizerController.addCustomPreset(it.name, onSuccess = {
                            oneTimeEventsChannel.trySend(AddPresetUiEvent.Success)
                        }, onFailure = {
                            oneTimeEventsChannel.trySend(AddPresetUiEvent.Error)
                        })
                    }
                }
            }
        }
    }
}

sealed interface AddPresetUiEvent {
    data object Success: AddPresetUiEvent
    data object Error: AddPresetUiEvent
}

sealed interface AddPresetUiAction {
    data class AddCustomPreset(val name: String) : AddPresetUiAction
}