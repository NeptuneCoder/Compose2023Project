package com.snw.samllnewweather.repository

import com.snw.samllnewweather.net.WeatherInfoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val api: WeatherInfoService) {

}