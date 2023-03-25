package com.example.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.livedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val gameViewModel by viewModels<GameViewModel> {
        GameViewModelProvider(application)
    }

    //        ViewModelProvider(
//            viewModelStore,
//            CustomAndroidViewModelFactory(application, GameViewModel::application)
//        ).get(GameViewModel::class.java)
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(binding.root)
        binding.model = gameViewModel
        binding.lifecycleOwner = this
        gameViewModel.inputContent.observe(this, {
            Log.i("tag", "inputContent = $it")
        })
    }
}