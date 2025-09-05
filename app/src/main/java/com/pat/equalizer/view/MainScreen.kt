package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pat.equalizer.components.EqualizerSlider
import com.pat.equalizer.core.model.Band
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.navigation.EqualizerScreen
import com.pat.equalizer.viewmodel.EqualizerUiState
import com.pat.equalizer.viewmodel.MainAction
import com.pat.equalizer.viewmodel.MainUiState
import com.pat.equalizer.viewmodel.MainViewModel

@Composable
fun MainScreen(navController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    MainScreen(
        state = mainViewModel.getCurrentState(),
        navController= navController,
        onPresetClick = { id ->
            mainViewModel.emitAction(MainAction.UsePreset(id))
        },
        onBandLevelChanged = { preset, band, level ->
            mainViewModel.emitAction(MainAction.OnBandLevelChanged(preset, band, level.toInt().toShort()))
        },
        onEqualizerSwitchChange = {
            mainViewModel.emitAction(MainAction.SetEqualizerSwitchState(it))
        },
        onBassBoostSwitchChange = {
            mainViewModel.emitAction(MainAction.SetBassBoostSwitchState(it))
        },
        onStrengthLevelChange = {
            mainViewModel.emitAction(MainAction.SetBassBoostStrength(it))
        })
}

@Composable
private fun MainScreen(
    state: MainUiState,
    navController: NavHostController,
    onPresetClick: (Preset) -> Unit = {},
    onBandLevelChanged: BandLevelChange = { _, _, _ -> },
    onEqualizerSwitchChange: (Boolean) -> Unit = { },
    onBassBoostSwitchChange: (Boolean) -> Unit = { },
    onStrengthLevelChange: (Int) -> Unit = { }
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(EqualizerScreen.AddNewPreset.route) }) {
            Icon(imageVector = Icons.Default.PlaylistAddCircle, contentDescription = null)
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            EqualizerSection(
                state = state,
                onBandLevelChanged = onBandLevelChanged,
                onEqualizerSwitchChange = onEqualizerSwitchChange,
                onPresetClick = onPresetClick
            )

            BassBoostSection(
                state = state,
                onBassBoostSwitchChange = onBassBoostSwitchChange,
                onStrengthLevelChange = onStrengthLevelChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EqualizerSection(
    state: MainUiState,
    onBandLevelChanged: BandLevelChange = { _, _, _ -> },
    onEqualizerSwitchChange: (Boolean) -> Unit = { },
    onPresetClick: (Preset) -> Unit = {}
) {
    val selectedPreset = state.equalizer.presets.firstOrNull { it.selected }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Equalizer")
        Switch(checked = state.equalizer.switchState, onCheckedChange = {
            onEqualizerSwitchChange(it)
        })
    }

    selectedPreset?.let {
        EqualizerBars(selectedPreset, selectedPreset.bands, onBandLevelChanged)

        PresetsDropdown(presets = state.equalizer.presets, onPresetClick = { preset ->
            onPresetClick(preset)
        })
    } ?: LoadingIndicator()
}

@Composable
fun BassBoostSection(
    state: MainUiState,
    onBassBoostSwitchChange: (Boolean) -> Unit,
    onStrengthLevelChange: (Int) -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Bass boost")
        Switch(checked = state.bassBoost.switchState, onCheckedChange = {
            onBassBoostSwitchChange(it)
        })
    }

    var sliderValue by remember { mutableFloatStateOf(state.bassBoost.strength.toFloat()) }

    LaunchedEffect(state.bassBoost.strength) {
        sliderValue = state.bassBoost.strength.toFloat()
    }

    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        onValueChangeFinished = { onStrengthLevelChange(sliderValue.toInt()) },
        valueRange = state.bassBoost.range.first.toFloat()..state.bassBoost.range.last.toFloat(),
        enabled = state.bassBoost.switchState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetsDropdown(presets: List<Preset>, onPresetClick: (preset: Preset) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(presets.firstOrNull { it.selected }?.name) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                presets.forEach {
                    DropdownMenuItem(text = { Text(it.name) }, onClick = {
                        selectedText = it.name
                        expanded = false
                        onPresetClick(it)
                    })
                }
            }
        }
    }
}

@Composable
fun EqualizerBars(
    preset: Preset,
    bands: List<Band>,
    onBandLevelChanged: BandLevelChange
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        bands.forEachIndexed { i, band ->
            var bandValue by rememberSaveable { mutableFloatStateOf(band.level.toFloat()) }

            LaunchedEffect(band.level) {
                bandValue = band.level.toFloat()
            }

            EqualizerSlider(
                topText = band.hzCenterFrequency,
                value = bandValue,
                valueRange = band.range.first.toFloat()..band.range.last.toFloat(),
                onValueChange = { newValue ->
                    bandValue = newValue
                },
                onValueChangeFinished = {
                    onBandLevelChanged(preset, i, bandValue)
                },
                enabled = preset.isCustom
            )
        }
    }
}

typealias BandLevelChange = (preset: Preset, bandId: Int, level: Float) -> Unit

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        state = MainUiState(
            EqualizerUiState(presets = listOf(
                Preset(
                    name = "Normal",
                    id = 0,
                    bands = listOf(
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "60 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "230 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "460 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "910 Hz"),
                    ),
                    selected = true
                ),
                Preset(
                    name = "Custom",
                    id = 1,
                    bands = listOf(
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "60 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "230 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "460 Hz"),
                        Band(level = 0, range = IntRange(-1500, 1500), hzCenterFrequency = "910 Hz"),
                    ),
                    selected = false,
                    isCustom = true
                )
            )
        )),
        navController = rememberNavController()
    )

}