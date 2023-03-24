package com.example.rtmpdemo.channel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import com.example.rtmpdemo.LivePusher
import java.util.concurrent.Executors

class AudioChannel(context: Context?, private val livePusher: LivePusher) {
    private val channels = 2
    private var channelConfigs = 0
    private val audioRecord: AudioRecord
    private val executer by lazy { Executors.newSingleThreadExecutor() }
    var inputSamples = 0

    init {

        //int audioSource, 来源：
//        int sampleRateInHz, 采样率
//        int channelConfig, 声道数
//        int audioFormat, 采样位：表示每次采样的数据大小
//        int bufferSizeInBytes
        channelConfigs = if (channels == 2) {
            AudioFormat.CHANNEL_IN_STEREO
        } else {
            AudioFormat.CHANNEL_IN_MONO
        }
        val minBufferSize = AudioRecord.getMinBufferSize(
            44100,
            channelConfigs,
            AudioFormat.ENCODING_PCM_16BIT
        ) shl 1
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        livePusher.native_setAudioEncInfo(44100, channelConfigs);
        inputSamples = livePusher.inputSamples * 2//乘以2 的原因是一个样本是2个字节；16位
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            channelConfigs,
            AudioFormat.ENCODING_PCM_16BIT,
            if (minBufferSize > inputSamples) {
                minBufferSize
            } else {
                inputSamples
            }
        )

    }


    @Volatile
    var isLiving = false

    fun startLive() {
        isLiving = true
        executer.submit(AudioTask())
    }

    inner class AudioTask : Runnable {
        override fun run() {
            //启动录音机
            audioRecord.startRecording()
            val buffer = ByteArray(inputSamples)

            while (isLiving) {
                val len = audioRecord.read(buffer, 0, buffer.size)
                if (len > 0) {
                    livePusher.native_pushAudio(buffer)
                }

            }

            //停止录音机
            audioRecord.stop()

        }

    }

    fun stopLive() {}
}