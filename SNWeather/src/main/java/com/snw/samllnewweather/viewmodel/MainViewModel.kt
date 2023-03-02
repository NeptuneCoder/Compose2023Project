package com.snw.samllnewweather.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.snw.samllnewweather.db.WeatherInfoDao
import com.snw.samllnewweather.ext.*
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.model.HourInfo
import com.snw.samllnewweather.net.AddressInfoService
import com.snw.samllnewweather.net.WeatherInfoService
import com.snw.samllnewweather.screen.WeatherInfo
import com.snw.samllnewweather.screen.randomData
import com.snw.samllnewweather.utils.getCurrentDayTime
import com.snw.samllnewweather.utils.getCurrentHourTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherApi: WeatherInfoService,
    private val addressApi: AddressInfoService,
    @ApplicationContext private val context: Context,
    private val dao: WeatherInfoDao
) : ViewModel() {

    private val _showPlaceholder = MutableStateFlow(true)
    val showPlaceholder: StateFlow<Boolean>
        get() = _showPlaceholder.asStateFlow()

    private val _isRefresing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefresing.asStateFlow()

    private val _errMsg = MutableStateFlow("")
    val errMsg: StateFlow<String>
        get() = _errMsg.asStateFlow()
    private val _weatherData = MutableStateFlow<WeatherInfo>(randomData())
    val weatherData: StateFlow<WeatherInfo>
        get() = _weatherData.asStateFlow()
    private var updateTime: Long = 0
    private var loc = ""


    fun refresh(location: String = loc) {


        this.loc = location
        if (loc.isEmpty()) {
            return
        }
        viewModelScope.launch {
            _showPlaceholder.emit(true)
            _isRefresing.emit(true)
        }
        stopLocation()
        viewModelScope.launch {
            addressApi.getAddressInfo(location)
                .flowOn(Dispatchers.IO).collect {
                    val address = it.location[0]
                    val weatherInfoList = dao.getBaseInfo(address.id, address.name)
                    if (weatherInfoList.isEmpty()) {
                        //TODO 第一次加载数据
                        initLoadData(location)
                    } else {
                        val weatherInfo = weatherInfoList.findLastNewInfo()
                        if (System.currentTimeMillis() - weatherInfo.timestamp < 5 * 1000 * 60) {
                            //TODO 直接查询当前天气
                            loadHourDataByLocal(weatherInfo)
                            //
                        } else {
                            //TODO 立刻查询天气
                            dao.deleteCurrentDataById(weatherInfo)
                            loadCurrentDataByNet(loc)
                        }
                    }
                }
        }
    }

    private fun loadCurrentDataByNet(location: String) {
        viewModelScope.launch {
            addressApi.getAddressInfo(location)
                .zip(weatherApi.getRealTimeInfo(location)) { source0, source1 ->
                    val result = WeatherInfo()
                    result.locationGps = location
                    result.address = source0.location.get(0).name
                    with(source1.now) {
                        result.publishTime = source1.updateTime.formatTime()
                        result.temp = temp.formatTemp()

                        result.icon = context.applicationContext.resources.getIdentifier(
                            "icon_" + icon,
                            "drawable",
                            context.applicationContext.packageName
                        )
                        result.feelTemp = feelsLike
                        result.text = text
                        result.windDirect = windDir
                        result.windLevel = windScale
                    }
                    result
                }.flowOn(Dispatchers.IO).collect {
                    dao.insertBaseInfo(it)
                    loadHourDataByLocal(it)
                }
        }
    }


    /**
    TODO 判断最新小时数据;
    当前的事件小于最小的时间说明就是最新数据；否则更新小时数据;并把请求的最远时间的数据更新本地最小时间的数据
     */
    private fun loadHourDataByLocal(weatherInfo: WeatherInfo) {
        viewModelScope.launch {
            val hourInfoList = dao.getHourInfo(weatherInfo.cityId, weatherInfo.cityName)
            val findLastMinTime = hourInfoList.findLastMinHour()

            if (findLastMinTime.toHourDateLong() <= getCurrentHourTime()) {
                dao.deleteHourDataById(findLastMinTime)
                loadHourDataByNet(weatherInfo)
            } else {
                weatherInfo.futureHours = hourInfoList
                loadDayDataByLocal(weatherInfo)
            }
        }
    }

    private fun loadHourDataByNet(weatherInfo: WeatherInfo) {
        viewModelScope.launch {
            weatherApi.getHourInfo(loc).flowOn(Dispatchers.IO).collect {
                //TODO 更新本地数据，拿到时间最大的插入到本地
                dao.insertHourInfo(it.hourly.findLastMaxHour())
                weatherInfo.futureHours = dao.getHourInfo(weatherInfo.cityId, weatherInfo.cityName)
                loadDayDataByLocal(weatherInfo)
            }
        }
    }

    /**
    TODO 判断最新小时数据;
    当前的事件小于最小的时间说明就是最新数据；否则更新小时数据;并把请求的最远时间的数据更新本地最小时间的数据
     */
    private fun loadDayDataByLocal(weatherInfo: WeatherInfo) {
        viewModelScope.launch {
            val dayInfoList = dao.getDayInfo(weatherInfo.cityId, weatherInfo.cityName)
            val findLastMinTime = dayInfoList.findLastMinDay()
            if (findLastMinTime.toDayDateLong() < getCurrentDayTime()) {
                dao.deleteDayDataById(findLastMinTime)
                loadDayDataByNet(weatherInfo)
            } else {
                weatherInfo.futureDays = dayInfoList
                _weatherData.emit(weatherInfo)
                _showPlaceholder.emit(false)
                _isRefresing.emit(false)
            }

        }
    }

    private fun loadDayDataByNet(weatherInfo: WeatherInfo) {
        viewModelScope.launch {
            weatherApi.getDayInfo(loc).flowOn(Dispatchers.IO).collect {
                dao.insertDayInfo(it.daily.findLastMaxDay())
                weatherInfo.futureDays = dao.getDayInfo(weatherInfo.cityId, weatherInfo.cityName)
                _weatherData.emit(weatherInfo)
                _showPlaceholder.emit(false)
                _isRefresing.emit(false)
            }
        }
    }

    private fun initLoadData(location: String) {
        viewModelScope.launch {

            //请求网络加载数据
            addressApi.getAddressInfo(location)
                .zip(weatherApi.getRealTimeInfo(location)) { source0, source1 ->
                    val result = WeatherInfo()
                    result.locationGps = location
                    with(source0.location.get(0)) {
                        result.address = name
                        result.cityId = id
                        result.cityName = name
                    }
                    with(source1.now) {
                        result.publishTime = source1.updateTime.formatTime()
                        result.temp = temp.formatTemp()

                        result.icon = context.applicationContext.resources.getIdentifier(
                            "icon_" + icon,
                            "drawable",
                            context.applicationContext.packageName
                        )
                        result.feelTemp = feelsLike
                        result.text = text
                        result.windDirect = windDir
                        result.windLevel = windScale
                    }
                    result
                }
                .zip(weatherApi.getHourInfo(location)) { result, source2 ->
                    source2.hourly.formatResourceId(context.applicationContext)
                    result.futureHours = source2.hourly
                    result
                }.zip(weatherApi.getDayInfo(location)) { source3, source4 ->
                    val dayInfo = source4.daily[0]
                    source3.riseTime = dayInfo.sunrise
                    source3.downTime = dayInfo.sunset
                    source3.tempMax = dayInfo.tempMax.formatTemp()
                    source3.tempMin = dayInfo.tempMin.formatTemp()
                    source4.daily.formatResourceId(context.applicationContext)
                    source3.futureDays = source4.daily
                    source3
                }.zip(weatherApi.getAirInfo(location)) { source5, source6 ->
                    source5.airAqi = source6.now.aqi
                    source5.airState = source6.now.category

                    source5
                }.map { weather ->
                    dao.insertBaseInfo(weather)
                    weather.futureHours.forEach {
                        it.cityId = weather.cityId
                        it.cityName = weather.cityName
                        dao.insertHourInfo(it)
                    }
                    weather.futureDays.forEach {
                        it.cityId = weather.cityId
                        it.cityName = weather.cityName
                        dao.insertDayInfo(it)
                    }
                    weather
                }.flowOn(Dispatchers.IO)
                .catch {
                    Log.i("throwable", "throwable == $it")
                    _isRefresing.emit(false)
                    _errMsg.emit(it.toString() + "loc" + location)
                }.collect {
                    updateTime = System.currentTimeMillis()
                    _weatherData.emit(it)
                    _showPlaceholder.emit(false)
                    _isRefresing.emit(false)
                }
        }
    }


    lateinit var mLocationClient: LocationClient
    private val myListener = MyLocationListener(this)

    init {
        initLocaionClient()
    }

    fun startLocation() {
        mLocationClient.start()
    }

    private fun initLocaionClient() {
        mLocationClient = LocationClient(context.applicationContext)
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener)

        val option = LocationClientOption()
        option.locationMode =
            LocationClientOption.LocationMode.Hight_Accuracy //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll") //可选，默认gcj02，设置返回的定位结果坐标系

        val span = 1000
        option.setScanSpan(span) //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true) //可选，设置是否需要地址信息，默认不需要

        option.isOpenGps = true //可选，默认false,设置是否使用gps

        option.isLocationNotify = true //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true) //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true) //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false) //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false) //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false) //可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        mLocationClient.locOption = option

    }


    fun stopLocation() {
        mLocationClient.stop()
    }

    /**
     * 实现定位回调
     */
    class MyLocationListener(val viewModel: MainViewModel) : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            if (location != null) {
                //获取纬度信息
                val latitude: Double = location.getLatitude()
                //获取经度信息
                val longitude: Double = location.getLongitude()
                if (location.locType == 61 || location.locType == 161) {
                    if (!viewModel.isRefreshing.value) {//避免定位成功后多次调用
                        viewModel.refresh(
                            "$longitude,$latitude"
                        )
                    }
                }
            } else {
                Log.i("location", "latitude = ")
            }
        }
    }


}