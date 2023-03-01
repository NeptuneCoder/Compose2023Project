package com.snw.samllnewweather.net

import android.util.Log
import com.coder.vincent.sharp_retrofit.call_adapter.flow.FlowCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetModule {
    //官方文档地址 ：https://dev.qweather.com/docs/api/weather/weather-now/
    private val WEATHER_BASE_URL = "https://api.qweather.com/"
    private val ADDRESS_BASE_URL = "https://geoapi.qweather.com/"

    @Singleton
    @Provides
    fun providerLoggerInterceptor(): HttpLoggingInterceptor {
        //日志显示级别
        //日志显示级别
        val level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
        //打印请求和返回参数
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("zcb", "OkHttp====Message:$message")
            }

        })
        loggingInterceptor.setLevel(level)
        //定制OkHttp
        return loggingInterceptor
    }

    //https://v0.yiketianqi.com/api?unescape=1&version=v61&appid=21563178&appsecret=xyYGxRA4
    @Singleton
    @Provides
    fun providerInterceptor(): Interceptor {
        return object : Interceptor {
            private val METHOD_GET = "GET"
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val requestBuilder = request.newBuilder();
                var urlBuilder = request.url.newBuilder();
                if (METHOD_GET.equals(request.method)) {
//                    appid=21563178&appsecret=xyYGxRA4
                    //测试账号：appid=43656176&appsecret=I42og6Lm
                    urlBuilder.addEncodedQueryParameter("key", "dc418e957f504a0ea777f9e91ae88329")
                    val httpUrl = urlBuilder.build()
                    requestBuilder.url(httpUrl);
                }

                return chain.proceed(requestBuilder.build())
            }

        }

    }

    @Singleton
    @Provides
    fun provideHttpClient(
        paramInterceptor: Interceptor,
        loggerInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .apply {
                readTimeout(15, TimeUnit.SECONDS)
                connectTimeout(15, TimeUnit.SECONDS)
                //设置超时
                writeTimeout(20, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)//错误重连
                proxy(Proxy.NO_PROXY)//设置不要代理
                addInterceptor(paramInterceptor)
                addInterceptor(loggerInterceptor)

            }.build()

    }

    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return createConverterFactory()//这里全局处理了code异常的提醒
    }


    @Singleton
    @Provides
    @Named("weatherUrl")
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("addressUrl")
    fun provideAddressRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ADDRESS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providerWeatherService(@Named("weatherUrl") retrofit: Retrofit): WeatherInfoService =
        retrofit.create(WeatherInfoService::class.java)

    @Singleton
    @Provides
    fun providerAddressService(@Named("addressUrl") retrofit: Retrofit): AddressInfoService =
        retrofit.create(AddressInfoService::class.java)


}