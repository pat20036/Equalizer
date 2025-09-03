package com.pat.equalizer.core.model

data class Preset(
    val name: String,
    val id: Int,
    val bands: List<Band>,
    val selected: Boolean = false,
    val isCustom: Boolean = false
)