package com.snw.samllnewweather.model

data class AirInfoResponse(
    val code: String,
    val fxLink: String,
    val now: AirNowInfo,
    val refer: Refer,
    val station: List<Station>,
    val updateTime: String
)