package com.snw.samllnewweather.ext

import android.content.Context
import android.util.Log
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo
import com.snw.samllnewweather.screen.WeatherInfo
import java.util.*

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

fun DayInfo.toDayDateLong(): Long {
    return (ymd.parse(this.fxDate) ?: Date()).time
}


fun HourInfo.formatResourceId(context: Context) {
    this.iconId = context.applicationContext.resources.getIdentifier(
        "icon_$icon",
        "drawable",
        context.applicationContext.packageName
    )
    Log.i("iconId", "icon  = $icon      iconId == ${iconId}")

}

fun HourInfo.toHourDateLong(): Long {
    //2023-03-02T17:00+08:00
    return (ymdhmD8.parse(this.fxTime) ?: Date()).time
}


fun List<HourInfo>.findLastMinHour(): HourInfo {
    return if (this.size == 1) {
        this.first()
    } else {
        this.reduce { a: HourInfo, b: HourInfo ->
            if (a.toHourDateLong() < b.toHourDateLong()) {
                a
            } else {
                b
            }
        }
    }
}


fun List<HourInfo>.findLastMaxHour(): HourInfo {
    return if (this.size == 1) {
        this.first()
    } else {
        this.reduce { a: HourInfo, b: HourInfo ->
            if (a.toHourDateLong() > b.toHourDateLong()) {
                a
            } else {
                b
            }
        }
    }
}


fun List<DayInfo>.findLastMinDay(): DayInfo {
    return if (this.size == 1) {
        this.first()
    } else {
        this.reduce { a: DayInfo, b: DayInfo ->
            if (a.toDayDateLong() < b.toDayDateLong()) {
                a
            } else {
                b
            }
        }
    }
}


fun List<DayInfo>.findLastMaxDay(): DayInfo {
    return if (this.size == 1) {
        this.first()
    } else {
        this.reduce { a: DayInfo, b: DayInfo ->
            if (a.toDayDateLong() > b.toDayDateLong()) {
                a
            } else {
                b
            }
        }
    }
}

fun List<WeatherInfo>.findLastNewInfo(): WeatherInfo {
    return if (this.size == 1) {
        this.first()
    } else {
        this.reduce { a: WeatherInfo, b: WeatherInfo ->
            if (a.timestamp < b.timestamp) {
                a
            } else {
                b
            }
        }
    }
}
