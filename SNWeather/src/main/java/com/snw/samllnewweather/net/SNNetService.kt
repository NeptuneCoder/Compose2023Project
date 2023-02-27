package com.snw.samllnewweather.net

import com.snw.samllnewweather.model.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query


interface SNNetService {
    //根据经纬度查询天气信息
    //https://api.qweather.com/v7/weather/now?location=121.502206,31.172141&key=dc418e957f504a0ea777f9e91ae88329
    @GET("v7/weather/now")
    fun getRealTimeInfo(
        @Query("location") location: String,
    ): Flow<RealTimeInfoResponse>

    @GET("v7/weather/7d")
    fun getDayInfo(
        @Query("location") location: String,
    ): Flow<DayInfoResponse>

    @GET("v7/weather/24h")
    fun getHourInfo(
        @Query("location") location: String,
    ): Flow<HourInfoResponse>

    @GET("v7/air/now")
    fun getAirInfo(
        @Query("location") location: String,
    ): Flow<AirInfoResponse>


}