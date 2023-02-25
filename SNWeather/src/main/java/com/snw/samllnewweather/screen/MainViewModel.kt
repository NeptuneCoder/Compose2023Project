package com.snw.samllnewweather.screen

import androidx.compose.material.BottomDrawerValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snw.samllnewweather.net.NetUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {


    private val _isRefresing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefresing.asStateFlow()

    private val _weatherData = MutableStateFlow<WeatherInfo>(randomData())
    val weatherData: StateFlow<WeatherInfo>
        get() = _weatherData.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefresing.emit(true)
            //请求网络加载数据
            delay(2000)
            _weatherData.emit(randomData())
            _isRefresing.emit(false)

        }
    }

    fun ruquestData() {
        NetUtil.service.listRepos("")
    }
}