package com.pat.equalizer.loudness.core

import com.pat.equalizer.bassboost.core.BassBoostController
import com.pat.equalizer.bassboost.core.model.BassBoostConfiguration
import com.pat.equalizer.core.EqualizerConfiguration
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.core.model.Band
import com.pat.equalizer.core.model.Preset
import com.pat.equalizer.viewmodel.EqualizerUiState
import com.pat.equalizer.viewmodel.MainAction
import com.pat.equalizer.viewmodel.MainUiState
import com.pat.equalizer.viewmodel.MainViewModel
import com.pat.equalizer.virtualizer.core.VirtualizerController
import com.pat.equalizer.virtualizer.core.model.VirtualizerConfiguration
import com.pat.equalizer.volume.core.VolumeController
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MainViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    //base mocks to avoid exceptions
    private var equalizerController = mock<EqualizerController> { on { configuration } doReturn MutableStateFlow(EqualizerConfiguration()) }
    private var bassBoostController = mock<BassBoostController> { on { configuration } doReturn MutableStateFlow(BassBoostConfiguration()) }
    private var virtualizerController = mock<VirtualizerController> { on { configuration } doReturn MutableStateFlow(VirtualizerConfiguration()) }
    private var volumeController = mock<VolumeController>()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(
            equalizerController = equalizerController,
            bassBoostController = bassBoostController,
            virtualizerController = virtualizerController,
            volumeController = volumeController
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should update selected preset when UsePreset action is emitted`() {
        val preset1 = Preset(id = 1, name = "Preset 1", bands = listOf())
        val preset2 = Preset(id = 2, name = "Preset 2", bands = listOf())

        val newConfigurationState = MutableStateFlow(
            EqualizerConfiguration(
                presets = listOf(
                    preset1, preset2.copy(selected = true)
                )
            )
        )

        whenever(equalizerController.configuration).thenReturn(newConfigurationState)
        viewModel.emitAction(MainAction.UsePreset(preset2))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(preset2.copy(selected = true), viewModel.state.value.equalizer.presets.firstOrNull { it.selected })
    }

    @Test
    fun `should update preset list when DeletePreset action is emitted`() {
        val preset1 = Preset(id = 1, name = "Preset 1", bands = listOf())
        val preset2 = Preset(id = 2, name = "Preset 2", bands = listOf())

        val configurationState = MutableStateFlow(
            EqualizerConfiguration(
                presets = listOf(
                    preset1,
                    preset2
                )
            )
        )

        whenever(equalizerController.configuration).thenReturn(configurationState)

        viewModel.state.value = MainUiState(
            equalizer = EqualizerUiState(
                presets = listOf(preset1, preset2)
            )
        )

        assertEquals(listOf(preset1, preset2), viewModel.state.value.equalizer.presets)

        val newConfigurationState = MutableStateFlow(
            EqualizerConfiguration(
                presets = listOf(
                    preset1
                )
            )
        )

        whenever(equalizerController.configuration).thenReturn(newConfigurationState)
        viewModel.emitAction(MainAction.DeletePreset(preset2))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(preset1), viewModel.state.value.equalizer.presets)
    }

    @Test
    fun `should update bands when OnBandLevelChanged action is emitted`() {
        val band = Band(level = 0, range = 0..1000, hzCenterFrequency = "100")

        val preset = Preset(
            id = 1,
            name = "Preset 1",
            bands = listOf(band),
            selected = true
        )

        viewModel.state.value = MainUiState(
            equalizer = EqualizerUiState(
                presets = listOf(preset)
            )
        )

        assertEquals(preset, viewModel.state.value.equalizer.presets.firstOrNull { it.id == preset.id })

        val expectedLevel = 500

        val configurationState = MutableStateFlow(
            EqualizerConfiguration(
                presets = listOf(
                    preset.copy(
                        bands = listOf(
                            band.copy(level = expectedLevel.toShort())
                        )
                    )
                )
            )
        )

        whenever(equalizerController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.OnBandLevelChanged(preset, bandId = 0, level = expectedLevel.toShort()))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(expectedLevel.toShort(), viewModel.state.value.equalizer.presets.firstOrNull { it.id == preset.id }?.bands?.firstOrNull()?.level)
    }

    @Test
    fun `should update switchState when SetEqualizerSwitchState action is emitted`() {
        val configurationState = MutableStateFlow(EqualizerConfiguration(enabled = true))
        whenever(equalizerController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetEqualizerSwitchState(true))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.state.value.equalizer.switchState)
    }

    @Test
    fun `should update switchState when SetBassBoostSwitchState action is emitted`() {
        val configurationState = MutableStateFlow(BassBoostConfiguration(enabled = true))
        whenever(bassBoostController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetEqualizerSwitchState(true))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.state.value.bassBoost.switchState)
    }

    @Test
    fun `should update strength when SetBassBoostStrength action is emitted`() {
        val strength = 1000
        val configurationState = MutableStateFlow(BassBoostConfiguration(enabled = true, strength = strength))
        whenever(bassBoostController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetBassBoostStrength(strength))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(strength, viewModel.state.value.bassBoost.strength)
    }

    @Test
    fun `should update switchState when SetVirtualizerSwitchState action is emitted`() {
        val enabled = true
        val configurationState = MutableStateFlow(VirtualizerConfiguration(enabled = enabled))
        whenever(virtualizerController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetVirtualizerSwitchState(enabled))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(enabled, viewModel.state.value.virtualizer.switchState)
    }

    @Test
    fun `should update strength when SetVirtualizerStrength action is emitted`() {
        val strength = 1000
        val configurationState = MutableStateFlow(VirtualizerConfiguration(strength = strength))
        whenever(virtualizerController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetVirtualizerStrength(strength))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(strength, viewModel.state.value.virtualizer.strength)
    }

    @Test
    fun `should update volume when SetVolumeLevel action is emitted`() {

        val expectedLevel = 20

        testDispatcher.scheduler.runCurrent()
        viewModel.emitAction(MainAction.SetVolumeLevel(expectedLevel))
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(expectedLevel, viewModel.state.value.volume.currentLevel)
    }

    @Test
    fun `should update loudnessEnhancerCheckboxState when SetEnhanceLoudness action is emitted`() {
        val configurationState = MutableStateFlow(EqualizerConfiguration(loudnessEnhancerEnabled = true))
        whenever(equalizerController.configuration).thenReturn(configurationState)

        viewModel.emitAction(MainAction.SetEqualizerSwitchState(true))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.state.value.equalizer.loudnessEnhancerCheckboxState)
    }
}