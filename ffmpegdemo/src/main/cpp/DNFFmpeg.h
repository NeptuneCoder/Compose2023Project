//
// Created by yanghai on 2023/3/10.
//

#ifndef COMPOSE2023PROJECT_DNFFMPEG_H
#define COMPOSE2023PROJECT_DNFFMPEG_H

#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
}


class DNFFmpeg {
public:
    DNFFmpeg(JavaCallHelper *callHelper, const char *url);

    ~DNFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void stop();

    void setRenderFrameCallback(RenderFrameCallback callback);

public:
    char *url;
    pthread_t pid;
    pthread_t pid_play;
    pthread_t pid_stop;
    AVFormatContext *formatContext = 0;
    JavaCallHelper *callHelper;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    RenderFrameCallback callback;
    bool isPlaying;
};


#endif //COMPOSE2023PROJECT_DNFFMPEG_H
