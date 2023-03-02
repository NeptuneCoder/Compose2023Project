package com.snw.samllnewweather.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "location_info_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0,
    val adm1: String,
    val adm2: String,
    val country: String,
    val fxLink: String,
    val id: String,
    val isDst: String,
    val lat: String,
    val lon: String,
    val name: String,
    val rank: String,
    val type: String,
    val tz: String,
    val utcOffset: String,
    val timeTamp: Long = System.currentTimeMillis()
) : Parcelable {
    fun lessThan20Min(): Boolean {
        return System.currentTimeMillis() - timeTamp > 20 * 1000 * 60
    }
}