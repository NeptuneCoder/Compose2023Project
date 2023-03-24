//
// Created by yanghai on 2023/3/20.
//
#include <jni.h>
#include <x264.h>
#include "librtmp/rtmp.h"
#include "SafeQueue.h"
#include "macro.h"
#include "VideoChannel.h"
#include "AudioChannel.h"

SafeQueue<RTMPPacket *> packets;
VideoChannel *videoChannel = 0;
AudioChannel *audioChannel = 0;
int isStart = 0;
pthread_t pid;
int readyPushing = 0;
uint32_t start_time = 0;


void releaseRTMPPacket(RTMPPacket **packet) {
    DELETE(packet);
}

void videoCallback(RTMPPacket *packet) {
    if (packet) {
        packet->m_nTimeStamp = RTMP_GetTime() - start_time;
        packets.push(packet);
    }
}

void audioCallback(RTMPPacket *packet) {
    if (packet) {
        packet->m_nTimeStamp = RTMP_GetTime() - start_time;
        packets.push(packet);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1init(JNIEnv *env, jobject thiz) {
    // TODO: implement native_init()
    //进行初始化的操作；准备一个video编码工具类进行编码
    videoChannel = new VideoChannel;
    //初始化编码器
    videoChannel->setRTMPPacketCallback(videoCallback);

    //准备一个队列，打包好的数据统一放入队列中；在线程中发送到服务器
    packets.setReleaseCallback(releaseRTMPPacket);

    audioChannel = new AudioChannel;
    audioChannel->setRTMPPacketCallback(audioCallback);

}


/**
 * 设置编码信息
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1setVideoEncInfo(JNIEnv *env, jobject thiz, jint width,
                                                             jint height, jint fps, jint bitrate) {
    if (videoChannel) {
        videoChannel->setVideoEncInfo(width, height, fps, bitrate);
    }
}


void *start(void *args) {
    char *url = static_cast<char *>(args);
    RTMP *rtmp = 0;
    do {
        rtmp = RTMP_Alloc();
        if (!rtmp) {
            LOGE("rtmp is null");
            break;
        }
        RTMP_Init(rtmp);
        rtmp->Link.timeout = 5;//5s
        int ret = RTMP_SetupURL(rtmp, url);
        if (!ret) {
            LOGE("rtmp 设置地址失败:%s", url);
            break;
        }
        RTMP_EnableWrite(rtmp);
        //链接服务器
        ret = RTMP_Connect(rtmp, 0);
        if (!ret) {
            LOGE("链接服务器失败");
            break;
        }
        start_time = RTMP_GetTime();
        readyPushing = 1;
        packets.setWork(1);

        audioCallback(audioChannel->getAudioTag());
        RTMPPacket *packet;
        while (isStart) {
            packets.pop(packet);
            if (!isStart) {
                break;
            }
            if (!packet) {
                continue;
            }
            packet->m_nInfoField2 = rtmp->m_stream_id;
            //1.表示队列
            ret = RTMP_SendPacket(rtmp, packet, 1);
            releaseRTMPPacket(&packet);
            if (!ret) {
                LOGE("发送失败");
                break;
            }
        }
        releaseRTMPPacket(&packet);
    } while (0);
    packets.setWork(0);
    packets.clear();
    if (rtmp) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
    }
    delete url;
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1start(JNIEnv *env, jobject thiz, jstring _path) {
    if (isStart) {
        return;
    }
    const char *path = env->GetStringUTFChars(_path, 0);
    isStart = 1;
    //这里copy一下是为了防止内存泄露造成的问题。
    char *url = new char[strlen(path) + 1];
    strcpy(url, path);
    pthread_create(&pid, 0, start, url);
    env->ReleaseStringUTFChars(_path, path);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1pushVideo(JNIEnv *env, jobject thiz,
                                                       jbyteArray _data) {
    if (!videoChannel || !readyPushing) {
        return;
    }
    jbyte *data = env->GetByteArrayElements(_data, 0);
    LOGE("data === %d\n", data[0]);
    videoChannel->encodeData(data);
    env->ReleaseByteArrayElements(_data, data, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1stop(JNIEnv *env, jobject thiz) {
    packets.setWork(0);
    packets.clear();
    DELETE(videoChannel);
    pthread_join(pid, 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1release(JNIEnv *env, jobject thiz) {
    // TODO: implement native_release()
    DELETE(videoChannel);
    DELETE(audioChannel);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1setAudioEncInfo(JNIEnv *env, jobject thiz,
                                                             jint sample_rate_in_hz,
                                                             jint channel_config) {
    if (audioChannel) {
        audioChannel->setVideoEncInfo(sample_rate_in_hz, channel_config);
    }
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_rtmpdemo_LivePusher_getInputSamples(JNIEnv *env, jobject thiz) {
    if (audioChannel) {
        return audioChannel->getInputSamples();
    }
    return -1;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_LivePusher_native_1pushAudio(JNIEnv *env, jobject thiz,
                                                       jbyteArray buffer) {
    if (!audioChannel || !readyPushing) {
        return;
    }
    jbyte *data = env->GetByteArrayElements(buffer, 0);


    LOGE("data === %d\n", data[0]);
    audioChannel->encodeData(data);
    env->ReleaseByteArrayElements(buffer, data, 0);

}