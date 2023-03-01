package com.snw.samllnewweather.model

data class AddressInfoResponse(
    val code: String,
    val location: List<Location>,
    val refer: Refer
)