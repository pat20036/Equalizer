package com.pat.equalizer.view

import NotificationPermissionRequester
import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.pat.equalizer.navigation.EqualizerNavHost
import com.pat.equalizer.service.EqualizerService
import com.pat.equalizer.ui.theme.EqualizerTheme
import com.pat.equalizer.viewmodel.MainAction
import com.pat.equalizer.viewmodel.MainViewModel
import com.pat.equalizer.volume.core.receiver.VolumeChangeReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService()

        setContent {
            EqualizerTheme {

                NotificationPermissionRequester(
                    shouldShowRationale = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) else false
                    },
                    onPermissionGranted = {
                        restartService()
                    }
                )

                val viewModel = hiltViewModel<MainViewModel>()

                registerVolumeReceiver { level ->
                    viewModel.emitAction(MainAction.SetVolumeLevel(level))
                }

                val navController = rememberNavController()

                EqualizerNavHost(navController = navController, mainViewModel = viewModel)
            }
        }
    }

    private fun registerVolumeReceiver(onVolumeLevelChange: (Int) -> Unit) {
        registerReceiver(
            this,
            VolumeChangeReceiver(onVolumeLevelChange),
            IntentFilter(INTENT_FILTER_VOLUME_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun startService() {
        ContextCompat.startForegroundService(this, Intent(this, EqualizerService::class.java))
    }

    private fun restartService() {
        stopService(Intent(this, EqualizerService::class.java))
        startService()
    }

    companion object {
        private const val INTENT_FILTER_VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    }
}