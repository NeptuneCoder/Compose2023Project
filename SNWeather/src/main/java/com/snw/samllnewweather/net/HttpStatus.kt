package com.snw.samllnewweather.net

import com.google.gson.annotations.SerializedName

data class HttpStatus(val code: Int, val message: String) {
    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    val isSucess: Boolean
        get() = code >= 0 && code <= ApiErrorCode.SUCCESS
}