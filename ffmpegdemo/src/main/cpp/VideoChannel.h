//
// Created by yanghai on 2023/3/11.
//

#ifndef COMPOSE2023PROJECT_VIDEOCHANNEL_H
#define COMPOSE2023PROJECT_VIDEOCHANNEL_H

#include "BaseChannel.h"

extern "C" {
#include "libswscale/swscale.h"
};

typedef void (*RenderFrameCallback)(uint8_t *, int, int, int);

class VideoChannel : public BaseChannel {
public:
    VideoChannel(int index, AVCodecContext *context);

    ~VideoChannel();

    void play();

    void decode();

    void setIsPlaying(int isPlaying);

    void render();

    void setRenderFrameCallback(RenderFrameCallback callback);

private:
    pthread_t decode_pid;
    pthread_t render_pid;
    SafeQueue<AVFrame *> avFrames;
    SwsContext *swsContext;
    RenderFrameCallback callback;
};

#endif //COMPOSE2023PROJECT_AUDIOCHANNEL_H
