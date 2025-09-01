package com.pat.equalizer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EqualizerSlider(
    topText: String,
    value: Float,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = -1500f..1500f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    Column {
        Text(topText)
        VerticalSlider(
            state = rememberSliderState(
                value = value,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange
            ),
            modifier = modifier
        )
    }
    Spacer(modifier = Modifier.size(6.dp))
}

@Preview(showBackground = true)
@Composable
private fun EqualizerSliderPreview() {
    EqualizerSlider("100 Hz", 100f)
}