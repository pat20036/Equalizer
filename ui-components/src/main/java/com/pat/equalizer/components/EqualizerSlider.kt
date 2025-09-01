package com.pat.equalizer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun EqualizerSlider(
    topText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = -1500f..1500f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    Row {
        Spacer(modifier = Modifier.size(6.dp))
        Column {
            Text(topText)
            Slider(
                onValueChangeFinished = onValueChangeFinished,
                steps = steps,
                valueRange = valueRange,
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(22.dp)
                    .graphicsLayer {
                        rotationZ = 270f
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxHeight,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(-placeable.width, 0)
                        }
                    }
                    .then(modifier)
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerSliderPreview() {
    EqualizerSlider("100 Hz", 100f, onValueChange = {})
}