package com.pat.equalizer.model

data class Preset(
    val name: String,
    val id: Int,
    val bands: List<Band>,
    val selected: Boolean = false,
    val isCustom: Boolean = false
)