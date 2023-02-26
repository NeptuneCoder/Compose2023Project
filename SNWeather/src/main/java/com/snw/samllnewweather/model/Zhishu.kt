package com.snw.samllnewweather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Zhishu(
    val chenlian: Chenlian,
    val chuanyi: Chuanyi,
    val daisan: Daisan,
    val diaoyu: Diaoyu,
    val ganmao: Ganmao,
    val kaiche: Kaiche,
    val liangshai: Liangshai,
    val lvyou: Lvyou,
    val xiche: Xiche,
    val ziwaixian: Ziwaixian
): Parcelable