package com.pat.equalizer.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.pat.equalizer.navigation.EqualizerNavHost
import com.pat.equalizer.service.EqualizerService
import com.pat.equalizer.ui.theme.EqualizerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService()

        setContent {
            EqualizerTheme {
                val navController = rememberNavController()

                EqualizerNavHost(navController = navController)
            }
        }
    }

    private fun startService() {
        ContextCompat.startForegroundService(this, Intent(this, EqualizerService::class.java))
    }
}