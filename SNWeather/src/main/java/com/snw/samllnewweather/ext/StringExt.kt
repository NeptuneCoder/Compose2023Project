package com.snw.samllnewweather.ext

import java.text.SimpleDateFormat

var sdf1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX")
val defaultTimeFormat = SimpleDateFormat("HH:mm")
fun String.formatTime(format: SimpleDateFormat = defaultTimeFormat): String {
    return format.format(sdf1.parse(this))
}

fun String.formatTemp(): String {
    return this.plus("°")
}