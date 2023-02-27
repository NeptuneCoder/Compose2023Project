package com.snw.samllnewweather.model

data class HourInfoResponse(
    val code: String,
    val fxLink: String,
    val hourly: List<HourInfo>,
    val refer: Refer,
    val updateTime: String
)