package com.pat.equalizer.model

data class Preset(val name: String, val id: Short, val bandLevels: List<BandLevel>, val selected: Boolean = false, val isCustom: Boolean = false)
