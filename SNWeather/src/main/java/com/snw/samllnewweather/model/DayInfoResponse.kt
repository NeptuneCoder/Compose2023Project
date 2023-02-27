package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayInfoResponse(
    val code: String,
    val daily: List<DayInfo>,
    val fxLink: String,
    val refer: Refer,
    val updateTime: String
) : Parcelable