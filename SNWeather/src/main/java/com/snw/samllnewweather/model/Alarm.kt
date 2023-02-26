package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alarm(
    val alarm_content: String,
    val alarm_level: String,
    val alarm_type: String
) : Parcelable