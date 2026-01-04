package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pat.equalizer.R
import com.pat.equalizer.components.BaseSettingsItem
import com.pat.equalizer.components.ScreenTitleAppBar
import com.pat.equalizer.modifiers.defaultHorizontalPadding
import com.pat.equalizer.viewmodel.MainAction
import com.pat.equalizer.viewmodel.MainViewModel

@Composable
fun SettingsScreen(navController: NavHostController) {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val state = mainViewModel.getCurrentState()
    SettingsScreen(
        navController = navController,
        loudnessEnhancerState = state.equalizer.loudnessEnhancerCheckboxState,
        onChangeLoudnessSetting = {
            mainViewModel.emitAction(MainAction.SetEnhanceLoudness(it))
        })
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    loudnessEnhancerState: Boolean,
    onChangeLoudnessSetting: (Boolean) -> Unit = {}
) {
    Scaffold(topBar = {
        ScreenTitleAppBar(text = stringResource(R.string.settings_screen_title), backAction = { navController.popBackStack() })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .defaultHorizontalPadding()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BaseSettingsItem(
                title = stringResource(R.string.loudness_enhancer_setting_title),
                description = stringResource(R.string.loudness_enhancer_setting_description),
                icon = Icons.Default.GraphicEq
            ) {
                Switch(checked = loudnessEnhancerState, onCheckedChange = {
                    onChangeLoudnessSetting(it)
                })
            }
        }
    }
}