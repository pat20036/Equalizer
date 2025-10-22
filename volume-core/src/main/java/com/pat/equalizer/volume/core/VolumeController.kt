package com.pat.equalizer.volume.core

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface VolumeController {
    fun getCurrentVolumeLevel(): Int
}

class VolumeControllerImpl @Inject constructor(@param:ApplicationContext private val context: Context) : VolumeController {
    override fun getCurrentVolumeLevel(): Int {
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }
}