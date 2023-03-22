package com.example.viewbindingdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.viewbindingdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content1.text = "内容一"
        binding.content2.text = "内容二"
        binding.content3.text = "内容三"
    }
}