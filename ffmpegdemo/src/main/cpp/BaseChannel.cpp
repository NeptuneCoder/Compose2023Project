//
// Created by yanghai on 2023/3/13.
//

#include "BaseChannel.h"
//
//BaseChannel::BaseChannel(int index, AVCodecContext *context, AVRational rational)
//        : index(index), codecContext(context), rational(rational) {
//    packets.setReleaseCallback(BaseChannel::releaseAvPacket);
//    frames.setReleaseCallback(BaseChannel::releaseAvFrame);
//}
//
////
////void BaseChannel::releaseAvPacket(AVPacket **avPacket) {
////    if (avPacket) {
////        av_packet_free(avPacket);
////        *avPacket = 0;
////    }
////}
////
////void BaseChannel::releaseAvFrame(AVFrame **avFrame) {
////    if (avFrame) {
////        av_frame_free(avFrame);
////        *avFrame = 0;
////    }
////}
//
//BaseChannel::~BaseChannel() {
//    frames.clear();
//    packets.clear();
//}
//
//void BaseChannel::stop() {
//
//}


//void BaseChannel::pause() {
//
//}