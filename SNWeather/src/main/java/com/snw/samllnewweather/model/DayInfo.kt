package com.snw.samllnewweather.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlinx.parcelize.Parcelize

@Entity(tableName = "day_info_table")
@Parcelize
data class DayInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val cloud: String,
    val fxDate: String,
    val humidity: String,
    val iconDay: String,
    var iconDayId: Int,
    val iconNight: String,
    var iconNightId: Int,
    val moonPhase: String,
    val moonPhaseIcon: String,
    val moonrise: String,
    val moonset: String,
    val precip: String,
    val pressure: String,
    val sunrise: String,
    val sunset: String,
    val tempMax: String,
    val tempMin: String,
    val textDay: String,
    val textNight: String,
    val uvIndex: String,
    val vis: String,
    val wind360Day: String,
    val wind360Night: String,
    val windDirDay: String,
    val windDirNight: String,
    val windScaleDay: String,
    val windScaleNight: String,
    val windSpeedDay: String,
    val windSpeedNight: String,
    var cityName: String,
    var cityId: String
) : Parcelable


