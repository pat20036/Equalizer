package com.pat.equalizer.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preset(
    val name: String,
    val id: Int,
    val bands: List<Band>,
    val selected: Boolean = false,
    val isCustom: Boolean = false
) : Parcelable