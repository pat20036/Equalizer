package com.pat.equalizer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pat.equalizer.view.AddPresetScreen
import com.pat.equalizer.view.MainScreen
import kotlinx.serialization.Serializable

@Composable
fun EqualizerNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = EqualizerScreen.Main.route) {
         composable(EqualizerScreen.Main.route) {
             MainScreen(navController = navController)
         }
         composable(EqualizerScreen.AddNewPreset.route) {
             AddPresetScreen(navController = navController)
         }
    }
}

@Serializable
sealed class EqualizerScreen(val route: String) {
    @Serializable
    data object Main : EqualizerScreen("main")

    @Serializable
    data object AddNewPreset : EqualizerScreen("addNewPreset")
}
