package com.snw.samllnewweather.ext

import java.text.SimpleDateFormat
import java.util.Date

var ymdhmD8: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX")
var ymdh: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH")
var ymd: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
val defaultTimeFormat = SimpleDateFormat("HH:mm")

fun String.toDate(): Date {
    return ymdhmD8.parse(this) ?: Date()
}

fun String.formatTime(format: SimpleDateFormat = defaultTimeFormat): String {
    return format.format(this.toDate())
}

fun String.formatTemp(): String {
    return this.plus("°")
}