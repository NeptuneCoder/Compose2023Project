//
// Created by yanghai on 2023/3/11.
//

#ifndef COMPOSE2023PROJECT_AUDIOCHANNEL_H
#define COMPOSE2023PROJECT_AUDIOCHANNEL_H

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "BaseChannel.h"

extern "C" {
#include <libswresample/swresample.h>
};

class AudioChannel : public BaseChannel {
public:

    AudioChannel(int index, AVCodecContext *context);

    ~AudioChannel();

    //创建两个线程，一个用来解码，一个用来播放
    void play();

    void playAudio();

    void decodeAudio();

    int getPcm();

public:
    pthread_t decode_audio_pid;
    pthread_t play_audio_pid;
    SLObjectItf engineObject = 0;
    SLEngineItf engineInterface = 0;
    SLObjectItf outputMixObject = 0;//混音器
    SLEnvironmentalReverbSettings reverbSettings;
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = 0;
    SLObjectItf bqPlayerObject = 0;
    SLPlayItf bqPlayerInterface = 0;
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueueInterface = 0;
    SwrContext *swrContext = 0;

    uint8_t *data = 0;
    int out_samplesize = 0;
    int out_channels = 0;
    int out_simple_rate = 0;
};

#endif //COMPOSE2023PROJECT_AUDIOCHANNEL_H
