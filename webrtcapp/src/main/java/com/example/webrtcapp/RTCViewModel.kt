package com.example.webrtcapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.webrtcapp.net.WebrtcService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RTCViewModel @Inject constructor(private val webrtcApi: WebrtcService) : ViewModel() {


    fun GetJoin(roomId: String) {
        viewModelScope.launch {
            webrtcApi.getJoinInfo(roomId).flowOn(Dispatchers.IO).catch {

            }.collect {

            }
        }
    }
}