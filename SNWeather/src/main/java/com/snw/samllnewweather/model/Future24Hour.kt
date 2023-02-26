package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Future24Hour(
    val aqi: String,
    val aqinum: String,
    val hours: String,
    val tem: String,
    val wea: String,
    val wea_img: String,
    val win: String,
    val win_speed: String
): Parcelable