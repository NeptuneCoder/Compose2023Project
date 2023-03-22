package com.example.databindingdemo

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    var image =
        "https://www.travelrich.com.tw/data/766716b8-48ea-4f5c-a080-cf9c6024d50a.jpg"
    var defaultImage = ""
    var adapter: RvAdapter
    val data = mutableListOf<User>()

    init {
        data.add(User("zhagnsan", 23))
        data.add(User("lisi", 22))
        data.add(User("wagnwu", 21))
        data.add(User("shenliu", 20))
        adapter = RvAdapter(data)
    }

    fun updateData() {
        data.addAll(randomData())
        adapter.notifyDataSetChanged()
    }

    fun randomData(): MutableList<User> {
        val data = mutableListOf<User>()
        data.add(User("zhagnsan", 23))
        data.add(User("lisi", 22))
        data.add(User("wagnwu", 21))
        data.add(User("shenliu", 20))
        return data
    }


}