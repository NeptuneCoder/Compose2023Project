package com.snw.samllnewweather.net

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import java.lang.reflect.Field
import java.lang.reflect.Method

fun createConverterFactory(): CustomGsonConverterFactory {
    val exclusionStrategy = object : ExclusionStrategy {

        override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return clazz == Field::class.java || clazz == Method::class.java
        }
    }
    val builder = GsonBuilder()
        .addSerializationExclusionStrategy(exclusionStrategy)
        .addDeserializationExclusionStrategy(exclusionStrategy)
    return CustomGsonConverterFactory.create(builder.create())
}