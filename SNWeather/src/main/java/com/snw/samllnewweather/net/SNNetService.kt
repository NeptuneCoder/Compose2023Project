package com.snw.samllnewweather.net

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface SNNetService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<String>>
}