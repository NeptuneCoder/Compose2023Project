package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NetWeatherInfo(
    val air: String,
    val air_level: String,
    val air_pm25: String,
    val air_tips: String,
    val alarm: Alarm,
    val aqi: Aqi,
    val city: String,
    val cityEn: String,
    val cityid: String,
    val country: String,
    val countryEn: String,
    val date: String,
    val hours: List<Future24Hour>,
    val humidity: String,
    val nums: Int,
    val pressure: String,
    val sunrise: String,
    val sunset: String,
    val tem: String,
    val tem1: String,
    val tem2: String,
    val update_time: String,
    val visibility: String,
    val wea: String,
    val wea_day: String,
    val wea_day_img: String,
    val wea_img: String,
    val wea_night: String,
    val wea_night_img: String,
    val week: String,
    val win: String,
    val win_meter: String,
    val win_speed: String,
    val zhishu: Zhishu
):Parcelable