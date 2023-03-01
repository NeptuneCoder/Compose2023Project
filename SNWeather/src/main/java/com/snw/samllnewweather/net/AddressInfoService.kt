package com.snw.samllnewweather.net

import com.snw.samllnewweather.model.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query


interface AddressInfoService {
    @GET("v2/city/lookup")
    fun getAddressInfo(
        @Query("location") location: String,
    ): Flow<AddressInfoResponse>

}