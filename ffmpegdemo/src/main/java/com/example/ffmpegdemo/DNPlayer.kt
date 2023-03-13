package com.example.ffmpegdemo

import android.view.SurfaceHolder
import android.view.SurfaceView

class DNPlayer : SurfaceHolder.Callback {
    external fun stringFromJNI(): String
    external fun newFun(array: IntArray, strs: Array<String>)
    private lateinit var errorListener: OnErrorListener
    private lateinit var onPrepaseListner: OnPreparsListener

    lateinit var holder: SurfaceHolder
    private lateinit var surfaceView: SurfaceView

    private lateinit var dataSource: String

    init {
        System.loadLibrary("nativeLib")
    }


    fun setDataSource(dataSource: String) {
        this.dataSource = dataSource
    }

    fun setSurfaceView(surfaceView: SurfaceView) {


        holder = surfaceView.holder
        //通过获得holder可以监听系统准备画布的状态
        holder.addCallback(this)
    }

    fun prepare() {
        native_prepare(dataSource)
    }

    fun onPrepase() {
        onPrepaseListner.onPrepase()
    }

    fun setOnPrepaseListener(onPrepaseListner: OnPreparsListener) {
        this.onPrepaseListner = onPrepaseListner
    }

    interface OnPreparsListener {
        fun onPrepase()
    }

    fun start() {
        native_start()
    }

    fun stop() {

    }

    fun release() {

    }

    fun onError(code: Int) {
        errorListener?.onError(code)
    }

    fun setOnErrorListener(errorListener: OnErrorListener) {
        this.errorListener = errorListener
    }

    interface OnErrorListener {
        fun onError(code: Int)
    }

    /**
     * ------------------------监听画布的状态-------------------------------------------------------
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        //画布创建成功了
    }

    /**
     * 画布改变了
     * 1. 横竖屏切换
     * 2. 按home都会回调该函数
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    /**
     * 销毁了
     * 1. 按home会被销毁
     * 2. 退出应用
     * 3. 脱离了window
     * 4. activity 被销毁了
     * 5. 只要画布不可见就会被销毁
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //当销毁时需要移除监听
        holder.removeCallback(this)
    }

    /**
     * ------------------------监听画布的状态-------------------------------------------------------
     */

    external fun native_prepare(dataSource: String)
    external fun native_start()
}

