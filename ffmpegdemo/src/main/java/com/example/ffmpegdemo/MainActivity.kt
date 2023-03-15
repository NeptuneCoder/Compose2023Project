package com.example.ffmpegdemo

import android.os.Bundle
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ffmpegdemo.ui.theme.Compose2023ProjectTheme

class MainActivity : ComponentActivity() {
    val dnPlayer by lazy { DNPlayer() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val surfaceView = findViewById<SurfaceView>(R.id.surface_view)
        val onStartBtn = findViewById<Button>(R.id.on_start)
        val onStopBtn = findViewById<Button>(R.id.on_stop)
        dnPlayer.setDataSource("http://39.134.65.162/PLTV/88888888/224/3221225611/index.m3u8") //rtmp://live.hkstv.hk.lxdns.com/live/hks")
        dnPlayer.setSurfaceView(surfaceView)
        dnPlayer.setOnPrepaseListener(object : DNPlayer.OnPreparsListener {
            override fun onPrepase() {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "准备妥当：", Toast.LENGTH_LONG)
                        .show()
                }
                dnPlayer.start()

            }

        })
        dnPlayer.setOnErrorListener(object : DNPlayer.OnErrorListener {
            override fun onError(code: Int) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "错误码：" + code, Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
        onStartBtn.setOnClickListener {
            dnPlayer.prepare()
        }
    }
}

