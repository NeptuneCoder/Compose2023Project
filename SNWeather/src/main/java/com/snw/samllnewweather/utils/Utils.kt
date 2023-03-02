package com.snw.samllnewweather.utils

import com.snw.samllnewweather.ext.ymd
import com.snw.samllnewweather.ext.ymdh
import java.util.*

fun getCurrentHourTime(): Long {
    return ymdh.parse(ymdh.format(Date())).time
}

fun getCurrentDayTime(): Long {
    return ymd.parse(ymd.format(Date())).time
}