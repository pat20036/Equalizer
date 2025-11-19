package com.pat.equalizer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pat.equalizer.R
import com.pat.equalizer.components.EqualizerSlider
import com.pat.equalizer.components.ScreenTitleAppBar
import com.pat.equalizer.components.SectionColumn
import com.pat.equalizer.components.SectionSwitch
import com.pat.equalizer.core.model.Band
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.modifiers.defaultHorizontalPadding
import com.pat.equalizer.navigation.EqualizerScreen
import com.pat.equalizer.view.components.PresetsDropdown
import com.pat.equalizer.viewmodel.BassBoostUiState
import com.pat.equalizer.viewmodel.EqualizerUiState
import com.pat.equalizer.viewmodel.MainAction
import com.pat.equalizer.viewmodel.MainUiState
import com.pat.equalizer.viewmodel.MainViewModel
import com.pat.equalizer.viewmodel.VirtualizerUiState

@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    MainScreen(
        state = mainViewModel.getCurrentState(),
        navController = navController,
        onPresetClick = { id ->
            mainViewModel.emitAction(MainAction.UsePreset(id))
        },
        onPresetDelete = {
            mainViewModel.emitAction(MainAction.DeletePreset(it))
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
        onBassBoostStrengthLevelChanged = {
            mainViewModel.emitAction(MainAction.SetBassBoostStrength(it))
        },
        onVirtualizerSwitchChange = {
            mainViewModel.emitAction(MainAction.SetVirtualizerSwitchState(it))
        },
        onVirtualizerStrengthLevelChanged = {
            mainViewModel.emitAction(MainAction.SetVirtualizerStrength(it))
        })
}

@Composable
private fun MainScreen(
    state: MainUiState,
    navController: NavHostController,
    onPresetClick: (Preset) -> Unit = {},
    onPresetDelete: (Preset) -> Unit = {},
    onBandLevelChanged: BandLevelChange = { _, _, _ -> },
    onEqualizerSwitchChange: (Boolean) -> Unit = { },
    onBassBoostSwitchChange: (Boolean) -> Unit = { },
    onBassBoostStrengthLevelChanged: (Int) -> Unit = { },
    onVirtualizerSwitchChange: (Boolean) -> Unit = { },
    onVirtualizerStrengthLevelChanged: (Int) -> Unit = { }
) {
    Scaffold(topBar = {
        ScreenTitleAppBar(text = stringResource(R.string.main_screen_title))
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(EqualizerScreen.AddNewPreset.route) }) {
            Icon(imageVector = Icons.Default.PlaylistAddCircle, contentDescription = null)
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .defaultHorizontalPadding()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            UserVolumeSection(state.volume.toFloat())

            EqualizerSwitch(
                switchState = state.equalizer.switchState,
                onEqualizerSwitchChange = onEqualizerSwitchChange
            )

            EqualizerSection(
                presets = state.equalizer.presets,
                onBandLevelChanged = onBandLevelChanged,
                onPresetClick = onPresetClick,
                onPresetDelete = onPresetDelete
            )

            BassBoostSection(
                state = state.bassBoost,
                onSwitchStateChange = onBassBoostSwitchChange,
                onStrengthLevelChange = onBassBoostStrengthLevelChanged
            )

            VirtualizerSection(
                state = state.virtualizer,
                onSwitchStateChange = onVirtualizerSwitchChange,
                onStrengthLevelChange = onVirtualizerStrengthLevelChanged
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserVolumeSection(level: Float, modifier: Modifier = Modifier) {
    var sliderValue by remember { mutableFloatStateOf(level) }

    LaunchedEffect(level) {
        sliderValue = level
    }

    SectionColumn {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialShapes.Cookie9Sided.toShape())
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Default.VolumeUp,
                    contentDescription = null
                )
            }
            Text(text = stringResource(R.string.volume_section_title))
        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..25f,
            enabled = false
        )
    }
}

@Composable
private fun EqualizerSwitch(
    switchState: Boolean,
    onEqualizerSwitchChange: (Boolean) -> Unit = { },
) {
    SectionColumn(modifier = Modifier.fillMaxWidth()) {
        SectionSwitch(
            text = stringResource(R.string.equalizer_section_title),
            icon = Icons.Default.Equalizer,
            checked = switchState,
            onSwitchStateChange = onEqualizerSwitchChange
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EqualizerSection(
    presets: List<Preset>,
    onBandLevelChanged: BandLevelChange = { _, _, _ -> },
    onPresetClick: (Preset) -> Unit = {},
    onPresetDelete: (Preset) -> Unit = {}
) {
    val selectedPreset = presets.firstOrNull { it.selected }

    SectionColumn {
        selectedPreset?.let {
            EqualizerBars(selectedPreset, selectedPreset.bands, onBandLevelChanged)

            PresetsDropdown(presets = presets, selectedPreset = selectedPreset, onPresetClick = { preset ->
                onPresetClick(preset)
            }, onPresetDelete = onPresetDelete)
        } ?: LoadingIndicator()
    }
}

@Composable
fun BassBoostSection(
    state: BassBoostUiState,
    onSwitchStateChange: (Boolean) -> Unit,
    onStrengthLevelChange: (Int) -> Unit = {}
) {
    SectionColumn {
        SectionSwitch(
            text = stringResource(R.string.bass_boost_section_title),
            icon = Icons.Default.GraphicEq,
            checked = state.switchState,
            onSwitchStateChange = onSwitchStateChange
        )

        var sliderValue by remember { mutableFloatStateOf(state.strength.toFloat()) }

        LaunchedEffect(state.strength) {
            sliderValue = state.strength.toFloat()
        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onStrengthLevelChange(sliderValue.toInt()) },
            valueRange = state.range.first.toFloat()..state.range.last.toFloat(),
            steps = 5,
            enabled = state.switchState
        )
    }
}

@Composable
fun VirtualizerSection(
    state: VirtualizerUiState,
    onSwitchStateChange: (Boolean) -> Unit,
    onStrengthLevelChange: (Int) -> Unit = {}
) {
    SectionColumn {
        SectionSwitch(
            text = stringResource(R.string.virtualizer_section_title),
            icon = Icons.Default.SpatialAudio,
            checked = state.switchState,
            onSwitchStateChange = onSwitchStateChange
        )

        var sliderValue by remember { mutableFloatStateOf(state.strength.toFloat()) }

        LaunchedEffect(state.strength) {
            sliderValue = state.strength.toFloat()
        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onStrengthLevelChange(sliderValue.toInt()) },
            valueRange = state.range.first.toFloat()..state.range.last.toFloat(),
            steps = 5,
            enabled = state.switchState
        )
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
            EqualizerUiState(
                presets = listOf(
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
            )
        ),
        navController = rememberNavController()
    )

}