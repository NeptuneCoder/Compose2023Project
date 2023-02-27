package com.snw.samllnewweather.error

/**
 * api异常类
 * Created by gaoneng on 17-9-11.
 */
open class ApiException(var code: Int, override var message: String, var param: Any? = null) : RuntimeException()