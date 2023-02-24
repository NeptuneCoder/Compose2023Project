package com.snw.samllnewweather.screen

import androidx.lifecycle.ViewModel
import com.snw.samllnewweather.netApi.NetUtil
import dagger.hilt.android.lifecycle.HiltViewModel


class MainViewModel : ViewModel() {
    fun ruquestData() {
        NetUtil.service.listRepos("")
    }
}