package com.example.datastore

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.datastore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val userPreferences by lazy { DataStoreUtil(this@MainActivity) }

    lateinit var databinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(databinding.root)

        databinding.save.setOnClickListener {
            val recordText = databinding.et.text.toString().trim()
            if (recordText.isEmpty()) {
                Toast.makeText(this@MainActivity, "is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launchWhenCreated {
                userPreferences.setUserName(recordText)
            }

        }
        databinding.showRecord.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                userPreferences.userNameFlow.collect {
                    databinding.record.text = it
                }
            }
        }

    }
}