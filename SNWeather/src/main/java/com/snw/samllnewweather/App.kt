package com.snw.samllnewweather

import android.app.Application
import com.baidu.location.LocationClient

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationClient.setAgreePrivacy(true)
    }
}