//
// Created by yanghai on 2023/3/9.
//
#ifndef COMPOSE2023PROJECT_DNFFMPEG_H
#define COMPOSE2023PROJECT_DNFFMPEG_H

#include <pthread.h>
#include <jni.h>
#include "macro.h"
#include "JavaCallHelper.h"
#include "VideoChannel.h"
#include "AudioChannel.h"


extern "C" {
#include <libavformat/avformat.h>
}


class DNFFmpeg {
private:
    char *dataSource;
    pthread_t pid;
    AVFormatContext *formatContext = 0;//包含了视频的信息，如宽高，
    JavaCallHelper *javaCallHelper;
    VideoChannel *videoChannel = 0;
    AudioChannel *audioChannel = 0;
    int isPlaying = 0;
    pthread_t start_pid;
    RenderFrameCallback callback;
public:
    DNFFmpeg(JavaCallHelper *javaCallHelper, const char *dataSource);

    ~DNFFmpeg();

    void prepare();

    //用于处理线程中初始化
    void _prepare();

    void start();

    void _start();

    void setRenderFrameCallback(RenderFrameCallback callback);

    void stop();
};

#endif //COMPOSE2023PROJECT_DNFFMPEG_H
