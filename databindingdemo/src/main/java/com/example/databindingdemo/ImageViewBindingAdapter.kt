package com.example.databindingdemo

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object ImageViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("image")
    fun setImageSrc(img: ImageView, image: String) {

        Picasso.with(img.context).load(image).into(img)
        Log.i("测试", "image == ${image}")
    }
}