package com.snw.samllnewweather.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

enum class ApiName(name: String) {
    WEATHER("weather"), AIR("air")
}

@Parcelize
@Entity(tableName = "statistics_info_table")
data class StatisticsApiCount(val type: ApiName, val count: Int = 0) : Parcelable
