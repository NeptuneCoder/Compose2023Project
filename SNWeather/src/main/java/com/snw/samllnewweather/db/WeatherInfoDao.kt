package com.snw.samllnewweather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo
import com.snw.samllnewweather.screen.WeatherInfo

@Dao
interface WeatherInfoDao {
    @Insert
    suspend fun insertBaseInfo(info: WeatherInfo)


    @Query("SELECT * FROM weather_info_table")
    suspend fun getBaseInfo(): List<WeatherInfo>

    @Insert
    suspend fun insertDayInfo(info: DayInfo)

    @Query("SELECT * FROM day_info_table")
    suspend fun getDayInfo(): List<DayInfo>

    @Insert
    suspend fun insertHourInfo(info: HourInfo)

    @Query("SELECT * FROM hour_info_table")
    suspend fun getHourInfo(): List<HourInfo>
}