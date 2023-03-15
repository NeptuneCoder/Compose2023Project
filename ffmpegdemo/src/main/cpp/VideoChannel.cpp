//
// Created by yanghai on 2023/3/11.
//
#include "VideoChannel.h"

#include <pthread.h>

extern "C" {
#include <libavformat/avformat.h>
#include "libavutil/imgutils.h"
}

VideoChannel::VideoChannel(int index, AVCodecContext *context) : BaseChannel(index, context) {

};


void *task_decode(void *args) {
    VideoChannel *channel = static_cast<VideoChannel *>(args);
    channel->decode();
    return 0;
}

void *task_render(void *args) {
    VideoChannel *channel = static_cast<VideoChannel *>(args);
    channel->render();
    return 0;
}

void VideoChannel::play() {
    isPlaying = 1;
    pthread_create(&decode_pid, 0, task_decode, this);
    pthread_create(&render_pid, 0, task_render, this);

}

void VideoChannel::decode() {
    AVPacket *packet = nullptr;
    while (isPlaying) {
        int ret = this->packets.pop(packet);
        if (!isPlaying) {//由于上面获取数据是需要等待，有可能在等待的过程中用户选择了停止，所以所以判断。
            break;
        }
        if (!ret) {//成功
            continue;
        }
        //把数据发给解码器
        ret = avcodec_send_packet(codecContext, packet);
//        if (ret == AVERROR(EAGAIN)) {
//          表示解码器中的数据太多急需处理
//            continue;
//        } else
        if (ret != 0) {
            break;
        }
        //读取一个图像
        AVFrame *frame = av_frame_alloc();
        //从解码器中读出数据
        ret = avcodec_receive_frame(codecContext, frame);
        if (ret == AVERROR(EAGAIN)) {//
            continue;
        } else if (ret != 0) {
            break;
        }
        avFrames.push(frame);
    }
    if (packet != nullptr) {
        releaseAvPacket(&packet);
    }

}


void VideoChannel::render() {
    //目标： RGBA
    swsContext = sws_getContext(
            codecContext->width, codecContext->height, codecContext->pix_fmt,
            codecContext->width, codecContext->height, AV_PIX_FMT_RGBA,
            SWS_BILINEAR, 0, 0, 0);
    AVFrame *frame = 0;
    //指针数组
    uint8_t *dst_data[4];
    int dst_linesize[4];
    av_image_alloc(dst_data, dst_linesize,
                   codecContext->width, codecContext->height, AV_PIX_FMT_RGBA, 1);
    while (isPlaying) {
        int ret = avFrames.pop(frame);
        if (!isPlaying) {
            break;
        }
        //src_linesize: 表示每一行存放的 字节长度
        sws_scale(swsContext, reinterpret_cast<const uint8_t *const *>(frame->data),
                  frame->linesize, 0,
                  codecContext->height,
                  dst_data,
                  dst_linesize);
        //回调出去进行播放
        callback(dst_data[0], dst_linesize[0], codecContext->width, codecContext->height);
        releaseAvFrame(&frame);
    }
    av_freep(&dst_data[0]);
    releaseAvFrame(&frame);
};

void VideoChannel::setRenderFrameCallback(RenderFrameCallback callback) {
    this->callback = callback;
}

void VideoChannel::setIsPlaying(int isPlaying) {
    this->isPlaying = isPlaying;
};


VideoChannel::~VideoChannel() {
    this->avFrames.setReleaseCallback(releaseAvFrame);
    this->avFrames.clear();
};

