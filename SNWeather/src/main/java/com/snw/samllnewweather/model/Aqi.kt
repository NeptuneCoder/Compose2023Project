package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Aqi(
    val air: String,
    val air_level: String,
    val air_tips: String,
    val co: String,
    val co_desc: String,
    val jinghuaqi: String,
    val kaichuang: String,
    val kouzhao: String,
    val no2: String,
    val no2_desc: String,
    val o3: String,
    val o3_desc: String,
    val pm10: String,
    val pm10_desc: String,
    val pm25: String,
    val pm25_desc: String,
    val so2: String,
    val so2_desc: String,
    val update_time: String,
    val waichu: String,
    val yundong: String
):Parcelable