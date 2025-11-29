package com.pat.equalizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.view.AddPresetScreen
import com.pat.equalizer.view.AddPresetScreenBackStackKey.ADDED_PRESET_ID_KEY
import com.pat.equalizer.view.MainScreen
import com.pat.equalizer.view.SettingsScreen
import com.pat.equalizer.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Composable
fun EqualizerNavHost(navController: NavHostController, mainViewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = EqualizerScreen.Main.route) {
        composable(EqualizerScreen.Main.route) { backStack ->
            val addedPreset by backStack
                .savedStateHandle
                .getStateFlow<Preset?>(ADDED_PRESET_ID_KEY, null)
                .collectAsState(initial = null)

            MainScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                addedPreset = addedPreset
            )

            LaunchedEffect(addedPreset) {
                if (addedPreset != null) {
                    backStack.savedStateHandle.remove<Preset>(ADDED_PRESET_ID_KEY)
                }
            }
        }
        composable(EqualizerScreen.AddNewPreset.route) {
            AddPresetScreen(navController = navController)
        }

        composable(EqualizerScreen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

@Serializable
sealed class EqualizerScreen(val route: String) {
    @Serializable
    data object Main : EqualizerScreen("main")

    @Serializable
    data object AddNewPreset : EqualizerScreen("addNewPreset")

    @Serializable
    data object Settings : EqualizerScreen("settings")
}
