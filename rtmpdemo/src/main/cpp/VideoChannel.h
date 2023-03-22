//
// Created by yanghai on 2023/3/21.
//

#ifndef COMPOSE2023PROJECT_VIDEOCHANNEL_H
#define COMPOSE2023PROJECT_VIDEOCHANNEL_H

#include "stdint.h"
#include "x264.h"
#include "librtmp/rtmp.h"
#include <pthread.h>
#include <cstring>
#include "macro.h"

typedef void (*RTMPPacketCallback)(RTMPPacket *packet);

class VideoChannel {
public:
    VideoChannel();

    ~VideoChannel();

    //设置编码器
    void setVideoEncInfo(int width, int height, int fps, int bitrate);

    void encodeData(int8_t *data);

    void setRTMPPacketCallback(RTMPPacketCallback callback);

private:
    //可能正在配置编码器时，推流的过程中可能改变编码器的大小从而导致多线程下造成编码器改变
    pthread_mutex_t mutex;
    int width;
    int height;
    int fps;
    int bitrate;
    x264_t *videoCodec = 0;
    x264_picture_t *pic_in;

    int ySize;//
    int uvSize;

    void sendSpsPps(uint8_t sps[100], uint8_t pps[100], int len, int len1);

    RTMPPacketCallback videoCallback;


    void sendFrame(int type, uint8_t *payload, int payload1);
};


#endif //COMPOSE2023PROJECT_VIDEOCHANNEL_H
