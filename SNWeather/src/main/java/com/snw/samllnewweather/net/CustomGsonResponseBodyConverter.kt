package com.snw.samllnewweather.net


import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.snw.samllnewweather.UTF_8
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
        Log.i("result", "response result = " + response)
        val httpStatus = gson.fromJson(response, HttpStatus::class.java)
            ?: throw ApiException(ApiErrorCode.UNKNOW, "自定义错误")

//        if (!httpStatus.token.isNullOrEmpty()) {
//            saveToken(httpStatus.token, LemaPayRetrofitClient.getCurrentToken())
//        }

//        if (httpStatus.code == REQUEST_AUTHORIZATION || httpStatus.code == APIERROR.NO_AUTHORIZATION) { //支付密码错误 需要取得wrong_times，余额不够，需要获取pay_type
//            val payException = GsonUtil.GsonToBean(response, DeviceLoginException::class.java)
//            value.close()
//            throw payException
//        }
//        if (httpStatus.code == APIERROR.NOT_BIND_ACCOUNT || httpStatus.code == APIERROR.BIND_THIRD_NEED_PHONE) { //支付密码错误 需要取得wrong_times，余额不够，需要获取pay_type
//            val jsonObject = JSONObject(response)
//            val code = jsonObject.optInt("code")
//            val msg = jsonObject.optString("msg")
//            val data = jsonObject.optJSONObject("data").toString()
//            val lemaException = OtherException(code, msg, data)
//            throw lemaException
//        }
//        if (httpStatus.code == PAY_PSW_ERRPR || httpStatus.code == LACK_OF_BALANCE) { //支付密码错误 需要取得wrong_times，余额不够，需要获取pay_type
//            val payException = GsonUtil.GsonToBean(response, PayException::class.java)
//            value.close()
//            throw payException
//        }
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
//            LogUtils.file("服务端返回了新token啦：token=$newToken")
//            /**
//             * 每次获取接口都去更新token
//             */
//            LemaPayRetrofitClient.addToken(newToken)
//
//            LemaPayApplication.getInstance().refreshToken(newToken)
        }
    }
}