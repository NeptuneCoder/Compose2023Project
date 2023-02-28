package com.snw.samllnewweather.screen

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo
import kotlinx.parcelize.Parcelize


val addressList: List<String> = listOf("上海.浦东新区", "四川.南江.乐坝", "合肥.蜀山区", "乌鲁木齐", "富蕴县")
val publishTimeList: List<String> = listOf("9:20", "2:29", "21:23")
val tempList: List<String> = listOf("22°C")
val riseTimeList: List<String> = listOf("5:23", "4:50", "5:25")
val downTimeList: List<String> = listOf("16:30", "18:20", "17:49")
val infoList: List<String> = listOf("多云:13°C~20°C", "太阳:20°C~30°C", "小雨:18°C~30°C", "大雨:16°C~20°C")
val bodyTempList: List<String> = listOf("29°C", "21°C", "39°C", "10°C", "30°C")
val windDirectList: List<String> = listOf("北风", "南飞", "西风", "东风")
val windLevelList: List<String> = listOf("2", "3", "4", "5", "9")
val airStateList: List<String> = listOf("优", "良", "中", "差")
val airLevelList: List<String> = listOf("98", "96", "94", "92", "90", "86", "84")
val chineseCalendarYearList: List<String> = listOf("甲寅", "癸丑")
val chineseCalendarDayList: List<String> = listOf("正月初二", "腊月初三", "二月初四", "二月初五", "二月初六", "三月初七")


val futureHoursList: List<List<FutureHourData>> = listOf(
    listOf(
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
    ), listOf(
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
    ), listOf(
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
    ), listOf(
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
    ), listOf(
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
        FutureHourData(time = chineseCalendarDayList.random(), state = infoList.random()),
    )
)

val futureDaysList: List<List<FutureDayData>> = listOf(
    listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    ), listOf(
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random()),
        FutureDayData(time = riseTimeList.random(), state = airStateList.random())
    )
)

@Parcelize
@Entity(tableName = "weather_info_table")
data class WeatherInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var address: String = "",
    var publishTime: String = "",
    var temp: String = "",
    var riseTime: String = "",
    var downTime: String = "",
    var tempMax: String = "",
    var tempMin: String = "",
    var text: String = "",
    var feelTemp: String = "",
    var windDirect: String = "",
    var windLevel: String = "",
    var airState: String = "",
    var airAqi: String = "",
    var locationGps: String = "",
    var futureHours: List<HourInfo> = listOf(),
    var futureDays: List<DayInfo> = listOf()
) : Parcelable

@Parcelize
data class FutureHourData(val time: String, val state: String) : Parcelable

@Parcelize
data class FutureDayData(val time: String, val state: String) : Parcelable

fun randomData(): WeatherInfo {
    val weatherData = WeatherInfo(
        address = addressList.random(),
        publishTime = publishTimeList.random(),
        temp = tempList.random(),
        riseTime = riseTimeList.random(),
        downTime = downTimeList.random(),
        text = infoList.random(),
        feelTemp = bodyTempList.random(),
        windDirect = windDirectList.random(),
        windLevel = windLevelList.random(),
        airState = airStateList.random(),
        airAqi = airLevelList.random(),
        futureHours = listOf(),
        futureDays = listOf()
    )
    return weatherData
}