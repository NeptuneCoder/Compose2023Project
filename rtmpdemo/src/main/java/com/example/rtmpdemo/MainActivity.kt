package com.example.rtmpdemo


import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        livePusher = LivePusher(this, 1920, 1080, 1000_000, 10, Camera.CameraInfo.CAMERA_FACING_BACK)
        //  设置摄像头预览的界面
        livePusher.setPreviewDisplay(surfaceView.holder)
        val bytes = byteArrayOf(0x22)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    }


    private lateinit var livePusher: LivePusher


    fun switchCamera(view: View?) {
        livePusher.switchCamera()
    }

    fun startLive(view: View?) {
        livePusher.startLive("rtmp://47.75.90.219/myapp/mystream")
    }

    fun stopLive(view: View?) {
        livePusher.stopLive()
    }
}