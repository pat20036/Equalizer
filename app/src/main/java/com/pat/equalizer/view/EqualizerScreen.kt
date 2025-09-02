package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pat.equalizer.components.EqualizerSlider
import com.pat.equalizer.model.BandLevel
import com.pat.equalizer.model.Preset
import com.pat.equalizer.viewmodel.EqualizerAction
import com.pat.equalizer.viewmodel.EqualizerUiState
import com.pat.equalizer.viewmodel.EqualizerViewModel
import kotlinx.coroutines.launch

@Composable
fun EqualizerScreen() {
    val equalizerViewModel: EqualizerViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    EqualizerScreen(
        state = equalizerViewModel.getCurrentState(), onPresetClick = { id ->
            coroutineScope.launch {
                equalizerViewModel.emitAction(EqualizerAction.UsePreset(id))
            }
        })
}

@Composable
private fun EqualizerScreen(
    state: EqualizerUiState,
    onPresetClick: (Preset) -> Unit = {},
) {
    val selectedPreset = state.presets.first { it.selected }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            EqualizerBars(selectedPreset.isCustom, selectedPreset.bandLevels)

            PresetsDropdown(presets = state.presets, onPresetClick = { preset ->
                onPresetClick(preset)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetsDropdown(presets: List<Preset>, onPresetClick: (preset: Preset) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(presets.firstOrNull()?.name) }

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
    isCustom: Boolean,
    levels: List<BandLevel>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        levels.forEachIndexed { i, bandLevel ->
            EqualizerSlider(
                topText = bandLevel.hzCenterFrequency,
                value = bandLevel.level.toFloat(),
                onValueChange = { newValue -> },
                onValueChangeFinished = {},
                enabled = isCustom
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerScreenPreview() {
    EqualizerScreen(
        state = EqualizerUiState(
            presets = listOf(
                Preset(
                    name = "Normal",
                    id = 0,
                    bandLevels = listOf(
                        BandLevel(level = 0, hzCenterFrequency = "60 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "230 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "910 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "3600 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "14000 Hz")
                    ),
                    selected = true
                ),
                Preset(
                    name = "Custom",
                    id = 1,
                    bandLevels = listOf(
                        BandLevel(level = 0, hzCenterFrequency = "60 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "230 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "910 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "3600 Hz"),
                        BandLevel(level = 0, hzCenterFrequency = "14000 Hz")
                    ),
                    selected = false,
                    isCustom = true
                )
            )
        )
    )

}