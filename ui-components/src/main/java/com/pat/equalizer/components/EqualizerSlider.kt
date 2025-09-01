package com.pat.equalizer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EqualizerSlider(
    topText: String,
    value: Float,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = -1500f..1500f,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val state = rememberSliderState(
        value = value,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange
    )

    // TODO temporary
    state.onValueChange = {
        state.value = it
        onValueChange(it)
    }

    Column {
        Text(topText)
        VerticalSlider(
            reverseDirection = true,
            state = state,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerSliderPreview() {
    EqualizerSlider("100 Hz", 100f, onValueChange = {}, onValueChangeFinished = {})
}