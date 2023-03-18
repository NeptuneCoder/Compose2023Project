//
// Created by yanghai on 2023/3/13.
//

#ifndef COMPOSE2023PROJECT_BASECHANNEL_H
#define COMPOSE2023PROJECT_BASECHANNEL_H

extern "C" {

#include "libavcodec/avcodec.h"
}

#include "SafeQueue.h"


class BaseChannel {
public:
    BaseChannel(int index, AVCodecContext *context);


    virtual ~BaseChannel();


    static void releaseAvPacket(AVPacket **avPacket);

    static void releaseAvFrame(AVFrame **avFrame);


public:
    int index;
    SafeQueue<AVPacket *> packets;//编码数据包队列
    SafeQueue<AVFrame *> avFrames;//解码数据包队列
    AVCodecContext *codecContext;

    int isPlaying = 0;



};

#endif //COMPOSE2023PROJECT_BASECHANNEL_H
