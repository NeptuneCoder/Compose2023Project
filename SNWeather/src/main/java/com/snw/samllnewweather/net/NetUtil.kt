package com.snw.samllnewweather.net

import retrofit2.Retrofit


object NetUtil {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build()
    }
    val service: SNNetService = retrofit.create(SNNetService::class.java)
}