package com.example.webrtcapp.net


import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.snw.samllnewweather.error.ApiException
import com.snw.samllnewweather.error.UpdateException

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader

internal class CustomGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        val httpStatus = gson.fromJson(response, HttpStatus::class.java) ?: throw ApiException(
            ApiErrorCode.UNKNOW,
            "自定义错误"
        )

        if (httpStatus.code == ApiErrorCode.FORCED_UPDATE) {
            //强制更新
            val checkVersion = Gson().fromJson(response, UpdateException::class.java)
            value.close()
            throw checkVersion
        }
        if (!httpStatus.isSucess) {
            value.close()
            var apiException = ApiException(httpStatus.code, httpStatus.message ?: "No Message")
            throw apiException
        }
        val contentType = value.contentType()
        val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset)
        val jsonReader = gson.newJsonReader(reader)

        try {
            return adapter.read(jsonReader)
        } finally {
            value.close()
        }
    }

    fun saveToken(newToken: String, oldToken: String) {
        if (newToken.trim() != oldToken.trim()) {
            //TODO 全局拦截更新token
            //可用于更新全局token
        }
    }
}