package com.pat.equalizer.bassboost.core

import android.media.audiofx.BassBoost
import com.pat.equalizer.bassboost.core.model.BassBoostConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface BassBoostController {
    val configuration: StateFlow<BassBoostConfiguration>
    suspend fun changeState(enabled: Boolean)
    suspend fun setStrength(strength: Int)
}

class BassBoostControllerImpl @Inject constructor(private val bassBoost: BassBoost, private val dataStore: BassBoostDataStore) : BassBoostController {

    private val _configuration = MutableStateFlow(BassBoostConfiguration())
    override val configuration: StateFlow<BassBoostConfiguration> = _configuration.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _configuration.value = dataStore.getConfiguration().first()

            configuration.collectLatest {
                bassBoost.apply {
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
        bassBoost.setStrength(strength.toShort())
        updateConfigurationState(strength = strength)
        dataStore.updateConfiguration(_configuration.value)
    }

    private fun updateConfigurationState(enabled: Boolean = configuration.value.enabled, strength: Int = configuration.value.strength) {
        _configuration.value = _configuration.value.copy(enabled = enabled, strength = strength)
    }
}