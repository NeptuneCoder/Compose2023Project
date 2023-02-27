package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RealTimeInfo(
    val code: Int,
    val fxLink: String,
    val now: NowInfo,
    val refer: Refer,
    val updateTime: String
) : Parcelable