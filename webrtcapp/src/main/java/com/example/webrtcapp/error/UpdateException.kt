package com.snw.samllnewweather.error

data class UpdateException(val url: String, val code: String) : RuntimeException() {
}