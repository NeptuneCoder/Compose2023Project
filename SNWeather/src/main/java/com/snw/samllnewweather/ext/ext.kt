package com.snw.samllnewweather.ext

import android.app.Application
import android.content.Context
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo

fun <T> List<T>.formatResourceId(context: Context) {
    this.forEach {
        if (it is DayInfo) {
            it.formatResourceId(context)
        } else if (it is HourInfo) {
            it.formatResourceId(context)
        }

    }
}

fun DayInfo.formatResourceId(context: Context) {
    this.iconDayId = context.applicationContext.resources.getIdentifier(
        "icon_$iconDay",
        "drawable",
        context.applicationContext.packageName
    )
    this.iconNightId = context.applicationContext.resources.getIdentifier(
        "icon_$iconNight",
        "drawable",
        context.applicationContext.packageName
    )
}


fun HourInfo.formatResourceId(context: Context) {
    this.iconId = context.applicationContext.resources.getIdentifier(
        "icon_$icon",
        "drawable",
        context.applicationContext.packageName
    )

}