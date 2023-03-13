//
// Created by yanghai on 2023/3/13.
//

#ifndef COMPOSE2023PROJECT_BASECHANNEL_H
#define COMPOSE2023PROJECT_BASECHANNEL_H

class BaseChannel {
public:
    BaseChannel(int index);

    virtual ~BaseChannel();

public:
    int index;
    //SafeQueue<AVPacket> packets;

};

#endif //COMPOSE2023PROJECT_BASECHANNEL_H
