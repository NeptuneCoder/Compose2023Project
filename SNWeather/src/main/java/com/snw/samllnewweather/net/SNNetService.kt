package com.snw.samllnewweather.net

import com.snw.samllnewweather.model.NetWeatherInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query


interface SNNetService {
    @GET("api?unescape=1&version=v62")
    fun getWeatherInfo(
        @Query("lng") lng: String,
        @Query("lat") lat: String
    ): Flow<NetWeatherInfo>
}