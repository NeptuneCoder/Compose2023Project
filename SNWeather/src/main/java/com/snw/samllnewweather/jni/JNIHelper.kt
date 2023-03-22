package com.snw.samllnewweather.jni

object JNIHelper {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getPrivateKey(): String
}