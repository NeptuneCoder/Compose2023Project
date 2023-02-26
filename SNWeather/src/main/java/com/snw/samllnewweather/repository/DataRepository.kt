package com.snw.samllnewweather.repository

import com.snw.samllnewweather.net.SNNetService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val api: SNNetService) {

}