package com.example.rtmpdemo;

import android.app.Activity;
import android.view.SurfaceHolder;

import com.example.rtmpdemo.channel.AudioChannel;
import com.example.rtmpdemo.channel.VideoChannel;

import org.jetbrains.annotations.NotNull;


public class LivePusher {


    static {
        System.loadLibrary("native-lib");
    }

    private AudioChannel audioChannel;
    private VideoChannel videoChannel;

    /**
     * @param activity
     * @param width    宽
     * @param height   高
     * @param bitrate  码率：码率越高视频越清晰，但是不是越大越好
     * @param fps      帧率：越高图形越连贯
     * @param cameraId 摄像头位前置还是后置
     */
    public LivePusher(Activity activity, int width, int height, int bitrate,
                      int fps, int cameraId) {
        native_init();
        videoChannel = new VideoChannel(this, activity, width, height, bitrate, fps, cameraId);
        audioChannel = new AudioChannel(activity.getApplicationContext(), this);
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        videoChannel.setPreviewDisplay(surfaceHolder);
    }

    public void switchCamera() {
        videoChannel.switchCamera();
    }

    public void startLive(String path) {
        native_start(path);
        videoChannel.startLive();
        audioChannel.startLive();
    }

    public void stopLive() {
        videoChannel.stopLive();
        audioChannel.stopLive();
        native_stop();
    }


    public native void native_init();

    public native void native_start(String path);

    public native void native_setVideoEncInfo(int width, int height, int fps, int bitrate);

    public native void native_setAudioEncInfo(int sampleRateInHz, int channelConfig);

    public native void native_pushVideo(byte[] data);

    public native void native_stop();

    public native void native_release();

    public native int getInputSamples();

    public native void native_pushAudio(@NotNull byte[] buffer);
}
