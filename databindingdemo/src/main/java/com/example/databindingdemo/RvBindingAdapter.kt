package com.example.databindingdemo

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object RvBindingAdapter {
    @JvmStatic//TODO kotlin中需要使用该注解否者会报错
    @BindingAdapter("adapter")
    fun bindRv(rv: RecyclerView, adapter: RvAdapter) {
        rv.adapter = adapter
        //todo 忘记设置这个导致显示不出来
        val manager = LinearLayoutManager(rv.context.applicationContext)
        rv.setLayoutManager(manager)
        Log.i("tag", "adapter.data.size = ${adapter.data.size}")
    }
}