package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Refer(
    val license: List<String>,
    val sources: List<String>
):Parcelable