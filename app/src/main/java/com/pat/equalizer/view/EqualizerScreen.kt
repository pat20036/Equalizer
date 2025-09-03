package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pat.equalizer.components.EqualizerSlider
import com.pat.equalizer.core.model.Band
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.viewmodel.EqualizerAction
import com.pat.equalizer.viewmodel.EqualizerUiState
import com.pat.equalizer.viewmodel.EqualizerViewModel
import kotlinx.coroutines.launch

@Composable
fun EqualizerScreen() {
    val equalizerViewModel: EqualizerViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    EqualizerScreen(
        state = equalizerViewModel.getCurrentState(),
        onPresetClick = { id ->
            coroutineScope.launch {
                equalizerViewModel.emitAction(EqualizerAction.UsePreset(id))
            }
        },
        addCustomPreset = {
            coroutineScope.launch {
                equalizerViewModel.emitAction(EqualizerAction.AddCustomPreset(it))
            }
        },
        onBandLevelChanged = { preset, band, level ->
            coroutineScope.launch {
                equalizerViewModel.emitAction(EqualizerAction.OnBandLevelChanged(preset, band, level.toInt().toShort()))
            }
        })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EqualizerScreen(
    state: EqualizerUiState,
    onPresetClick: (Preset) -> Unit = {},
    addCustomPreset: (String) -> Unit = {},
    onBandLevelChanged: BandLevelChange = { _, _, _ -> }
) {
    val selectedPreset = state.presets.firstOrNull { it.selected }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { addCustomPreset("Custom") }) {
            Icon(imageVector = Icons.Default.PlaylistAddCircle, contentDescription = null)
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            selectedPreset?.let {
                EqualizerBars(selectedPreset, selectedPreset.bands, onBandLevelChanged)

                PresetsDropdown(presets = state.presets, onPresetClick = { preset ->
                    onPresetClick(preset)
                })
            } ?: LoadingIndicator()
        }
    }
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
private fun EqualizerScreenPreview() {
    EqualizerScreen(
        state = EqualizerUiState(
            presets = listOf(
                Preset(
                    name = "Normal",
                    id = 0,
                    bands = listOf(
                        Band(level = 0, hzCenterFrequency = "60 Hz"),
                        Band(level = 0, hzCenterFrequency = "230 Hz"),
                        Band(level = 0, hzCenterFrequency = "910 Hz"),
                        Band(level = 0, hzCenterFrequency = "3600 Hz"),
                        Band(level = 0, hzCenterFrequency = "14000 Hz")
                    ),
                    selected = true
                ),
                Preset(
                    name = "Custom",
                    id = 1,
                    bands = listOf(
                        Band(level = 0, hzCenterFrequency = "60 Hz"),
                        Band(level = 0, hzCenterFrequency = "230 Hz"),
                        Band(level = 0, hzCenterFrequency = "910 Hz"),
                        Band(level = 0, hzCenterFrequency = "3600 Hz"),
                        Band(level = 0, hzCenterFrequency = "14000 Hz")
                    ),
                    selected = false,
                    isCustom = true
                )
            )
        )
    )

}