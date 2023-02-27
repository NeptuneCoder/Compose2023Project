package com.snw.samllnewweather.ext

import java.text.SimpleDateFormat

var sdf1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX")
val timeFormat = SimpleDateFormat("HH:mm")
fun String.formatTime(): String {
    return timeFormat.format(sdf1.parse(this))
}

fun String.formatTemp(): String {
    return this.plus("°")
}