package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Diaoyu(
    val level: String,
    val tips: String
): Parcelable