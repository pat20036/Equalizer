package com.pat.equalizer.volume.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class VolumeChangeReceiver(private val onVolumeLevelChange: (Int) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 0)) {
            AudioManager.STREAM_MUSIC -> {
                onVolumeLevelChange(getVolumeLevel(intent))
            }
        }
    }

    fun getVolumeLevel(intent: Intent): Int {
        return intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0)
    }
}