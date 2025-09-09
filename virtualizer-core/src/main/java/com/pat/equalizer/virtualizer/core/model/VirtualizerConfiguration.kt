package com.pat.equalizer.virtualizer.core.model

data class VirtualizerConfiguration(
    val strength: Int = 0,
    val enabled: Boolean = false,
    val range: IntRange = 0..1000,
)