//
// Created by yanghai on 2023/3/9.
//

#include "DNFFmpeg.h"
//#include <cstring>
/**
 * 在线程中调用准流程
 * @return
 */
void *task_prepare(void *args) {
    DNFFmpeg *dnfFmpeg = static_cast<DNFFmpeg *>(args);
    dnfFmpeg->_prepare();
    LOGE("这里在准备");
    return 0;//线程函数必须return
    //打开一个直播地址
    //AVFormatContext **ps 这就是一个视频信息相关的上下文
}

DNFFmpeg::DNFFmpeg(JavaCallHelper *javaCallHelper, const char *dataSource) {
    this->dataSource = new char[strlen(dataSource)];
    //将dataSource拷贝给this->dataSource
    strcpy(this->dataSource, dataSource);
    this->javaCallHelper = javaCallHelper;

}

DNFFmpeg::~DNFFmpeg() {
    /**
     * delete dataSource;
       dataSource = 0;
     */
    DELETE(dataSource);
    DELETE(javaCallHelper);
}


void DNFFmpeg::prepare() {
    pthread_create(&pid, 0, task_prepare, this);
}

void DNFFmpeg::_prepare() {
    //需要网络初始化，不然ffmpeg不能联网

    avformat_network_init();
    LOGE("这里在准备avformat_network_init = %s", dataSource);
    int state = avformat_open_input(&avFormatContext, dataSource, 0, 0);
    LOGE("这里在准备avformat_network_init:%d", state);
    if (state != 0) {//===0表示成功
        //反射调用
        javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        LOGE("这里在准备FFMPEG_CAN_NOT_OPEN_URL");
        return;
    }
    //查找媒体中的音视频流
    int errorCode = avformat_find_stream_info(avFormatContext, 0);
    if (errorCode < 0) {//>=0 表示成功；< 0 表示失败
        LOGE("这里在准备FFMPEG_CAN_NOT_FIND_STREAMS");
        javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        return;
    }
    LOGE(" avFormatContext->nb_streams === %d", avFormatContext->nb_streams);

    //通过找到的流打开编解码器;通过上面成功后avFormatContext->nb_streams就有值了。
    //表示av_stream的个数；
    // 什么是av_stream?一个视频里面包含的一个视频流，一个音频流；字幕一般也是一个流，但是一般和图形合在了一起。
    for (int i = 0; i < avFormatContext->nb_streams; i++) {
        LOGE("这里在准备 foreach");
        AVStream *avStream = avFormatContext->streams[i];
        LOGE("这里在准备 avStream = %p", &avStream);
        //如何判断当前的流是什么？
        //包含了 解码 这段流的各种参数信息，如码率；
        //什么是码流？越大图像越清晰codecParam->bit_rate
        AVCodecParameters *codecParam = avStream->codecpar;
        LOGE("这里在准备 codec_id = %d", codecParam->codec_id);
        //无论音频还是视频都需要处理的事情如下
        //1.获得解码器; 1.1 查找当前流 的编码方式
        const AVCodec *dec = avcodec_find_decoder(codecParam->codec_id);
        if (dec == NULL) {//如果当前打包的ffmpeg不支持该种解码器则可能返回NULL
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            LOGE("这里在准备FFMPEG_FIND_DECODER_FAIL");
            return;
        }
        //2. 获得解码器的上下文
        AVCodecContext *context = avcodec_alloc_context3(dec);
        if (context == NULL) {
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            LOGE("这里在准备FFMPEG_ALLOC_CODEC_CONTEXT_FAIL");
            return;
        }

        //3. 设置上下文的一些参数
        int errorCode = avcodec_parameters_to_context(context, codecParam);
        if (errorCode < 0) {
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            LOGE("这里在准备FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL");
            return;
        }

        //4. 打开解码器
        int openCode = avcodec_open2(context, dec, 0);
        if (openCode != 0) {
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            LOGE("这里在准备FFMPEG_OPEN_DECODER_FAIL");
            return;
        }
        LOGE("这里----到了这里了吗？");
        //一般播放器只处理两个流
        if (codecParam->codec_type == AVMEDIA_TYPE_AUDIO) {//音频
            audioChannel = new AudioChannel(i);
        } else if (codecParam->codec_type == AVMEDIA_TYPE_VIDEO) {//视频
            videoChannel = new VideoChannel(i);
        }

    }
    LOGE("这里----videoChannel %p", videoChannel);
    LOGE("这里----audioChannel %p", audioChannel);
    if (videoChannel == 0 && audioChannel == 0) {
        javaCallHelper->OnError(THREAD_CHILD, FFMPEG_NOMEDIA);
        return;
    }
    LOGE("这里成功了");
    //准备好了，开始回调java方法
    javaCallHelper->onPrepase(THREAD_CHILD);

}


void *task_start_play(void *args) {
    DNFFmpeg *dnfFmpeg = static_cast<DNFFmpeg * > (args);
    dnfFmpeg->_start();
    return 0;
}

void DNFFmpeg::start() {
    //应该避免在主线程中执行
    isPlaying = 1;

    pthread_create(&start_pid, 0, task_start_play, this);
}

/**
 * 专门读取数据包
 */
void DNFFmpeg::_start() {
    //1. 读取媒体数据包
    //2. 解码
    int ret = 0;
    while (isPlaying) {//如果处于播放中，就一直读取视频流
        AVPacket *avPacket = av_packet_alloc();//存放压缩后的数据
        //这里能够修改指针avPacket的数据
        ret = av_read_frame(avFormatContext, avPacket);
        if (ret == 0) {//表示成功
            //判断当前数据是音频还是视频？
            if (audioChannel && audioChannel->index == avPacket->stream_index) {//表示视频


            } else if (videoChannel && videoChannel->index == avPacket->stream_index) {

            }
        } else if (ret == AVERROR_EOF) {//end of file;读取完成还没有播放完成

        }
    }

}