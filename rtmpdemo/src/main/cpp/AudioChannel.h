//
// Created by yanghai on 2023/3/23.
//

#ifndef COMPOSE2023PROJECT_AUDIOCHANNEL_H
#define COMPOSE2023PROJECT_AUDIOCHANNEL_H

#include "faac.h"
#include "librtmp/rtmp.h"
#include "macro.h"
#include "pthread.h"

typedef void (*RTMPPacketCallback)(RTMPPacket *packet);

class AudioChannel {
public:
    AudioChannel();

    ~AudioChannel();


    void encodeData(int8_t *data);

public:
    void setVideoEncInfo(int sample_rate_hz, int channel_config);

    void setRTMPPacketCallback(RTMPPacketCallback callback);

    int getInputSamples();

    RTMPPacket *getAudioTag();

private:
    RTMPPacketCallback callback;
    unsigned long inputSamples;//输入给编码器的样本，要编码的数据的个数，如：采集到了100个数据交给了encoder；一次能输入到编码器的最大数据量

    unsigned long maxOutputBytes; //编码后输出的最大的字节数
    int mChannel = -1;
    faacEncHandle audioCodec;
    unsigned char *buffer;
    pthread_mutex_t mutex;
};


#endif //COMPOSE2023PROJECT_AUDIOCHANNEL_H
