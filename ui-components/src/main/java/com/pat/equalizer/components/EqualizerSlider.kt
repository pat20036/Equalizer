package com.pat.equalizer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EqualizerSlider(
    topText: String,
    value: Float,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(text = topText, style = MaterialTheme.typography.titleSmall)
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            enabled = enabled
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerSliderPreview() {
    EqualizerSlider("100 Hz", 100f, onValueChange = {}, valueRange = -1500f..1500f, onValueChangeFinished = {})
}