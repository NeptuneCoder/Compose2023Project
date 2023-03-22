package com.example.databindingdemo

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    var image =
        "https://www.travelrich.com.tw/data/766716b8-48ea-4f5c-a080-cf9c6024d50a.jpg"
    var defaultImage = ""
    var adapter: RvAdapter

    init {
        val data = listOf<User>(
            User("zhagnsan", 23),
            User("lisi", 22),
            User("wagnwu", 21),
            User("shenliu", 20)
        )
        adapter = RvAdapter(data)
    }

}