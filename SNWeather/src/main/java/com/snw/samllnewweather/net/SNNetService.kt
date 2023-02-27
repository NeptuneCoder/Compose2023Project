package com.snw.samllnewweather.net

import com.snw.samllnewweather.model.RealTimeInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query


interface SNNetService {
    //根据经纬度查询天气信息
    //https://api.qweather.com/v7/weather/now?location=121.502206,31.172141&key=dc418e957f504a0ea777f9e91ae88329
    @GET("v7/weather/now")
    fun getRealTimeInfo(
        @Query("location") location: String,
    ): Flow<RealTimeInfo>
}