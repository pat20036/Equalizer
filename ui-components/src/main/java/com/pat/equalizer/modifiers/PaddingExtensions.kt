package com.pat.equalizer.modifiers

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.defaultHorizontalPadding() = this.then(
    Modifier.padding(horizontal = 16.dp)
)