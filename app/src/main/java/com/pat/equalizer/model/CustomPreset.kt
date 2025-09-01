package com.pat.equalizer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomPreset(
    val name: String = "Custom",
    val bandLevel0: Short = 0,
    val bandLevel1: Short = 0,
    val bandLevel2: Short = 0,
    val bandLevel3: Short = 0,
    val bandLevel4: Short = 0
): Parcelable
