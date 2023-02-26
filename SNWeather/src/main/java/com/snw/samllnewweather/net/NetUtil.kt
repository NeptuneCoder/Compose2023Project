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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetUtil {
    val BASE_URL = "https://v0.yiketianqi.com/"

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
                    urlBuilder.addEncodedQueryParameter("appsecret", "I42og6Lm")
                    urlBuilder.addEncodedQueryParameter("appid", "43656176")
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
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(paramInterceptor)
            .addInterceptor(loggerInterceptor)
            .build()

    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideSNNetService(retrofit: Retrofit): SNNetService =
        retrofit.create(SNNetService::class.java)
}