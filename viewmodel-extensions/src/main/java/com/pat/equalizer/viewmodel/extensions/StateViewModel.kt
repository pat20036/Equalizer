package com.pat.equalizer.viewmodel.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("ComposableNaming")
abstract class StateViewModel<STATE_TYPE : BaseUiState, ACTION_TYPE> : ViewModel() {

    abstract val state: MutableStateFlow<STATE_TYPE>

    private val _action: MutableSharedFlow<ACTION_TYPE> = MutableSharedFlow(replay = 0)

    @Composable
    fun getCurrentState() = state.collectAsStateWithLifecycle().value

    fun updateState(value: STATE_TYPE) {
        state.update {
            value
        }
    }

    suspend fun emitAction(value: ACTION_TYPE) {
        _action.emit(value)
    }

    fun collectLatestAction(uiAction: (ACTION_TYPE) -> Unit) {
        viewModelScope.launch {
            _action.collectLatest {
                uiAction(it)
            }
        }
    }
}
