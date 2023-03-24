//
// Created by yanghai on 2023/3/23.
//

#include <pthread.h>
#include <cstring>
#include "AudioChannel.h"
#include "faac.h"


AudioChannel::AudioChannel() {
    pthread_mutex_init(&mutex, 0);
}

AudioChannel::~AudioChannel() {
//    delete buffer;
//    buffer = 0;
    DELETE(buffer);
    if (audioCodec) {
        faacEncClose(audioCodec);
        audioCodec = 0;
    }

    pthread_mutex_destroy(&mutex);
}

void AudioChannel::setVideoEncInfo(int sample_rate_hz, int channel_config) {
    this->mChannel = channel_config;
    //打开编码器
    this->audioCodec = faacEncOpen(sample_rate_hz, channel_config, &inputSamples,
                                   &maxOutputBytes);
    //设置编码器参数
    faacEncConfigurationPtr mfaacConfig = faacEncGetCurrentConfiguration(audioCodec);
    mfaacConfig->mpegVersion = MPEG4;//配置版本为4，
    mfaacConfig->aacObjectType = LOW;//低复杂度规格
    mfaacConfig->inputFormat = FAAC_INPUT_16BIT;//表示输入的的输入一位的大小
    mfaacConfig->outputFormat = 0;//0表示编码出原始格式，不会给头
    //设置编码器参数
    faacEncSetConfiguration(audioCodec, mfaacConfig);
    buffer = new unsigned char[maxOutputBytes];

}

void AudioChannel::setRTMPPacketCallback(RTMPPacketCallback callback) {
    this->callback = callback;
}


int AudioChannel::getInputSamples() {
    return maxOutputBytes;
}

void AudioChannel::encodeData(int8_t *data) {
    pthread_mutex_lock(&mutex);
    //进行编码;返回值是编码后的字节
    int len = faacEncEncode(audioCodec, reinterpret_cast<int32_t *>(data), inputSamples,
                            buffer,
                            maxOutputBytes);
    if (len > 0) {
        //组装数据
        int bodySize = 2 + len;
        RTMPPacket *packet = new RTMPPacket;
        RTMPPacket_Alloc(packet, bodySize);
        //具体组装数据
        packet->m_body[0] = 0xAF;//表示双声道
        if (mChannel == 1) {
            packet->m_body[0] = 0xAE;//表示单声道
        }

        packet->m_body[0] = 0x01;//编码出来的都是01

        memcpy(&packet->m_body[2], buffer, len);
        packet->m_hasAbsTimestamp = 0;
        packet->m_nBodySize = bodySize;
        packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
        packet->m_nChannel = 0x10;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        callback(packet);


    }

    pthread_mutex_unlock(&mutex);
}


RTMPPacket *AudioChannel::getAudioTag() {
    unsigned char *buf;
    unsigned long len;
    faacEncGetDecoderSpecificInfo(audioCodec, &buf, &len);
    int bodySize = 2 + len;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    //具体组装数据
    packet->m_body[0] = 0xAF;//表示双声道
    if (mChannel == 1) {
        packet->m_body[0] = 0xAE;//表示单声道
    }

    packet->m_body[0] = 0x01;//编码出来的都是01

    memcpy(&packet->m_body[2], buffer, len);
    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x10;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}