package com.pat.equalizer.viewmodel

import app.cash.turbine.test
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.core.model.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AddPresetViewModelTest {

    private val equalizerController: EqualizerController = mock()
    private lateinit var viewModel: AddPresetViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddPresetViewModel(equalizerController)
    }

    @After
    fun reset() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when AddCustomPreset action is sent and succeeds, emit Success event`() = runTest {
        val presetName = "Rock"
        val expectedPreset = mock<Preset>()

        whenever(equalizerController.addCustomPreset(eq(presetName), any(), any())).thenAnswer {
            val successCallback = it.arguments[1]

            @Suppress("UNCHECKED_CAST")
            val suspendCallback = successCallback as suspend (Preset) -> Unit

            runBlocking {
                suspendCallback.invoke(expectedPreset)
            }
        }

        viewModel.oneTimeEventsChannel.receiveAsFlow().test {
            viewModel.emitAction(AddPresetUiAction.AddCustomPreset(presetName))

            val event = awaitItem()

            assert(event is AddPresetUiEvent.Success)
            assertEquals(expectedPreset, (event as AddPresetUiEvent.Success).addedPreset)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AddCustomPreset action is sent and fails, emit Error event`() = runTest {
        val presetName = "Jazz"

        doAnswer {
            @Suppress("UNCHECKED_CAST")
            val onFailure = it.arguments[2] as () -> Unit
            onFailure.invoke()
        }.whenever(equalizerController).addCustomPreset(eq(presetName), any(), any())

        viewModel.oneTimeEventsChannel.receiveAsFlow().test {
            viewModel.emitAction(AddPresetUiAction.AddCustomPreset(presetName))

            val event = awaitItem()
            assertEquals(AddPresetUiEvent.Error, event)

            cancelAndIgnoreRemainingEvents()
        }
    }
}