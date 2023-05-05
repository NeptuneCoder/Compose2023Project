package com.example.webrtcapp.net

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface WebrtcService {
    @GET("r/")
    fun getAddressInfo(
        @Query("roomId") roomId: String,
    ): Flow<Any>

    @GET("join/{ROOMID}")
    fun getJoinInfo(
        @Path("ROOMID") roomId: String,
    ): Flow<Any>

    @GET
    fun GetIceServer(@Url url: Url)

}