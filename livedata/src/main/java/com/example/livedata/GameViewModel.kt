package com.example.livedata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class GameViewModel(private val application: Application) : AndroidViewModel(application) {
    //实现了单向数据源
    private val _currentScrambledWord = MutableLiveData<String>("test")
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    var inputContent = MutableLiveData<String>("")


    private var score = 0
    private var currentWordCount = 0
    private val _worldCount = MutableLiveData<String>(
        application.getString(R.string.word_count, 0, MAX_NO_OF_WORDS)
    )
    val worldCount: LiveData<String>
        get() = _worldCount

    private val _score = MutableLiveData<String>(
        application.getString(R.string.score, 0)
    )
    val scoreRef: LiveData<String>
        get() = _score


    fun onSkipWord() {
        _currentScrambledWord.value = getNextScrambledWord()
        currentWordCount++
        _worldCount.value =
            application.getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
    }

    fun onSubmitWord() {
        _currentScrambledWord.value = getNextScrambledWord()
        currentWordCount++

        _worldCount.value =
            application.getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        score += SCORE_INCREASE
        _score.value = application.getString(R.string.score, score)
    }


    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }
}