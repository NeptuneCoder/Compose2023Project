//
// Created by yanghai on 2023/3/11.
//

#ifndef COMPOSE2023PROJECT_AUDIOCHANNEL_H
#define COMPOSE2023PROJECT_AUDIOCHANNEL_H

#include "BaseChannel.h"

class AudioChannel : public BaseChannel {
public:

    AudioChannel(int index, AVCodecContext *context);

    ~AudioChannel();
};

#endif //COMPOSE2023PROJECT_AUDIOCHANNEL_H
