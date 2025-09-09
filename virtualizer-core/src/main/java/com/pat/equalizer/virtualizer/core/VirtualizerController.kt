package com.pat.equalizer.virtualizer.core

import android.media.audiofx.Virtualizer
import com.pat.equalizer.virtualizer.core.model.VirtualizerConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface VirtualizerController {
    val configuration: StateFlow<VirtualizerConfiguration>
    suspend fun changeState(enabled: Boolean)
    suspend fun setStrength(strength: Int)
}

@Suppress("DEPRECATION")
class VirtualizerControllerImpl @Inject constructor(private val virtualizer: Virtualizer, private val dataStore: VirtualizerDataStore) : VirtualizerController {

    private val _configuration = MutableStateFlow(VirtualizerConfiguration())
    override val configuration: StateFlow<VirtualizerConfiguration> = _configuration.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _configuration.value = dataStore.getConfiguration().first()

            configuration.collectLatest {
                virtualizer.apply {
                    enabled = it.enabled
                    setStrength(it.strength.toShort())
                }
            }
        }
    }

    override suspend fun changeState(enabled: Boolean) {
        updateConfigurationState(enabled = enabled)
        dataStore.updateConfiguration(_configuration.value)
    }

    override suspend fun setStrength(strength: Int) {
        virtualizer.setStrength(strength.toShort())
        updateConfigurationState(strength = strength)
        dataStore.updateConfiguration(_configuration.value)
    }

    private fun updateConfigurationState(enabled: Boolean = configuration.value.enabled, strength: Int = configuration.value.strength) {
        _configuration.value = _configuration.value.copy(enabled = enabled, strength = strength)
    }
}