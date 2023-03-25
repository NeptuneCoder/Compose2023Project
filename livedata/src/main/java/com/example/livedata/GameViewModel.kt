package com.example.livedata

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    //实现了单向数据源
    private val _currentScrambledWord = MutableLiveData<String>("test")
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    var inputContent = MutableLiveData<String>("")

    fun onSkipWord() {
        Log.i("tag", "onSkipWord")
    }

    fun onSubmitWord() {
        Log.i("tag", "onSubmitWord")
    }
}