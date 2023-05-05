package com.example.facetracing

import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facetracing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), Camera.PreviewCallback, SurfaceHolder.Callback {
    companion object {
        init {
            System.loadLibrary("nativeLib")
        }
    }

    lateinit var cameraHelper: CameraHelper
    lateinit var dataBinding: ActivityMainBinding
    val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(dataBinding.root)
        dataBinding.surface.holder.addCallback(this)

        cameraHelper = CameraHelper(cameraId)
        cameraHelper.setPreviewCallback(this)


    }

    override fun onResume() {
        super.onResume()
        native_init("/sdcard/lbpcascade_frontalface.xml")
        cameraHelper.startPreview()
    }

    override fun onStop() {
        super.onStop()
        native_release()
        cameraHelper.stopPreview()
    }

    /**
     *  this->onErrorId = env->GetMethodID(jclazz, "onError", "(I)V");
    this->onPrepaseId = env->GetMethodID(jclazz, "onPrepase", "()V");
     */
    fun onError(code: Int) {

    }

    fun onPrepase() {

    }


    external fun native_init(model: String)
    external fun native_setSurface(surface: Surface)
    external fun native_postData(data: ByteArray, width: Int, height: Int, cameraId: Int)
    external fun native_release()
    override fun onPreviewFrame(data: ByteArray, camera: Camera?) {
        native_postData(data, CameraHelper.WIDTH, CameraHelper.HEIGHT, cameraId)
        Log.i("tag", "这里有数据吗？")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        native_setSurface(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//        native_setSurface(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        TODO("Not yet implemented")
    }


}