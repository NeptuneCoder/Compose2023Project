package com.snw.samllnewweather.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "hour_info_table")
@Parcelize
data class HourInfo(
    val cloud: String,
    val dew: String,
    @PrimaryKey val fxTime: String,
    val humidity: String,
    val icon: String,
    var iconId: Int,
    val pop: String,
    val precip: String,
    val pressure: String,
    val temp: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String
) : Parcelable