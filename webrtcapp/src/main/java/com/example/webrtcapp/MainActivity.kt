package com.example.webrtcapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.webrtcapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        binding.next.setOnClickListener {
            val v = binding.input.text.toString()
            if (v.isEmpty()) {
                Toast.makeText(MainActivity@ this, "room id is empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(this, RTCActivity::class.java)
            intent.putExtra("roomId", v)
            startActivity(intent)
        }
        binding.randomRoomId.setOnClickListener {
            binding.input.setText(randomRoomId())
        }
    }

    fun randomRoomId(): String {
        val str = StringBuffer()
        List(8) { (Math.random() * 10).toInt() }.forEach {
            str.append(it)
        }
        return str.toString()
    }
}