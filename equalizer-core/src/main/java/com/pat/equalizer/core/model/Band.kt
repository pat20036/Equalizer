package com.pat.equalizer.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Band(val level: Short, val range: IntRange, val hzCenterFrequency: String): Parcelable
