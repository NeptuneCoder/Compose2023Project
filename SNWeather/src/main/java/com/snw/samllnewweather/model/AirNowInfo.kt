package com.snw.samllnewweather.model

data class AirNowInfo(
    val aqi: String,
    val category: String,
    val co: String,
    val level: String,
    val no2: String,
    val o3: String,
    val pm10: String,
    val pm2p5: String,
    val primary: String,
    val pubTime: String,
    val so2: String
)