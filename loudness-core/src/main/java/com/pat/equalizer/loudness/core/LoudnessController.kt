package com.pat.equalizer.loudness.core

import android.media.audiofx.LoudnessEnhancer
import javax.inject.Inject

// Experimental fix for system volume change - implemented as a part of equalizer api
interface LoudnessController {
    fun setEnabled(enabled: Boolean)
}

class LoudnessControllerImpl @Inject constructor(private val loudnessEnhancer: LoudnessEnhancer) : LoudnessController {

    override fun setEnabled(enabled: Boolean) {
        loudnessEnhancer.apply {
            setEnabled(enabled)
            setTargetGain(700) // default gain (safe value)
        }
    }
}