package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.pat.equalizer.model.CustomPreset
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
        }, onCustomPresetClick = {
            coroutineScope.launch {
                equalizerViewModel.emitAction(value = EqualizerAction.UseCustomPreset)
            }
        },
        onChangeBarValue = { band, level ->
            coroutineScope.launch {
                equalizerViewModel.emitAction(EqualizerAction.ChangeBandLevel(band, level))
                equalizerViewModel.emitAction(EqualizerAction.SetCustomPreset)
            }
        })
}

@Composable
private fun EqualizerScreen(
    state: EqualizerUiState,
    onPresetClick: (Short) -> Unit = {},
    onCustomPresetClick: () -> Unit = {},
    onChangeBarValue: (band: Short, level: Short) -> Unit = { _, _ -> }
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            EqualizerBars(state.levels) { band, level ->
                onChangeBarValue(band, level)
            }

            PresetsDropdown(presets = state.presets, onPresetClick = { id ->
                onPresetClick(id)
            }, customPreset = state.customPreset, onCustomPresetClick = {
                onCustomPresetClick()
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetsDropdown(presets: List<Preset>, customPreset: CustomPreset, onPresetClick: (id: Short) -> Unit, onCustomPresetClick: () -> Unit) {
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
                        onPresetClick(it.id)
                    })
                }

                DropdownMenuItem(text = { Text(customPreset.name) }, onClick = {
                    selectedText = customPreset.name
                    expanded = false
                    onCustomPresetClick()
                })
            }
        }
    }
}

@Composable
fun EqualizerBars(levels: List<BandLevel>, onBandLevelChanged: (band: Short, level: Short) -> Unit) {
    val bandBarValues = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f) }

    LaunchedEffect(levels) {
        bandBarValues.clear()
        bandBarValues.addAll(levels.map { it.level })
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in levels.indices) {
            EqualizerSlider(levels[i].hzCenterFrequency, value = bandBarValues[i], onValueChange = { bandBarValues[i] = it }, onValueChangeFinished = {
                onBandLevelChanged(i.toShort(), bandBarValues[i].toInt().toShort())
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerScreenPreview() {
    EqualizerScreen(
        state = EqualizerUiState(
            levels = listOf(
                BandLevel(0f, "60"),
                BandLevel(500f, "230"),
                BandLevel(-300f, "910"),
                BandLevel(0f, "3600"),
                BandLevel(1500f, "14000")
            ), presets = listOf(Preset("Normal", 0), Preset("Pop", 1), Preset("Rock", 2)), customPreset = CustomPreset(name = "Custom")
        )
    )
}