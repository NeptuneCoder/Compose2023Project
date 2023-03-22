//
// Created by yanghai on 2023/3/9.
//

#include "DNFFmpeg.h"

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
    this->dataSource = new char[strlen(dataSource) + 1];
    //将dataSource拷贝给this->dataSource
    strcpy(this->dataSource, dataSource);
    this->javaCallHelper = javaCallHelper;

}


void DNFFmpeg::prepare() {
    pthread_create(&pid, 0, task_prepare, this);
}

int ret = 0;

void DNFFmpeg::_prepare() {
    //需要网络初始化，不然ffmpeg不能联网
    avformat_network_init();
    AVDictionary *options = 0;
    //设置连接超时为5s
    av_dict_set(&options, "timeout", "5000000", 0);
    ret = avformat_open_input(&formatContext, dataSource, 0, &options);
    av_dict_free(&options);
    if (ret != 0) {//===0表示成功
        //反射调用
        javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        LOGE("这里在准备FFMPEG_CAN_NOT_OPEN_URL");
        return;
    }

    //查找媒体中的音视频流
    ret = avformat_find_stream_info(formatContext, 0);
    if (ret < 0) {//>=0 表示成功；< 0 表示失败
        LOGE("这里在准备FFMPEG_CAN_NOT_FIND_STREAMS");
        javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        return;
    }

    //通过找到的流打开编解码器;通过上面成功后avFormatContext->nb_streams就有值了。
    //表示av_stream的个数；
    // 什么是av_stream?一个视频里面包含的一个视频流，一个音频流；字幕一般也是一个流，但是一般和图形合在了一起。

    for (int i = 0; i < formatContext->nb_streams; ++i) {
        //可能代表是一个视频 也可能代表是一个音频
        AVCodecParameters *codecpar = formatContext->streams[i]->codecpar;
        AVStream *stream = formatContext->streams[i];
        //包含了 解码 这段流 的各种参数信息(宽、高、码率、帧率)
        AVRational avRational = formatContext->streams[i]->avg_frame_rate;
        //无论视频还是音频都需要干的一些事情（获得解码器）
        // 1、通过 当前流 使用的 编码方式，查找解码器
        const AVCodec *dec = avcodec_find_decoder(codecpar->codec_id);
        if (dec == NULL) {
            LOGE("查找解码器失败:%s", av_err2str(ret));
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            return;
        }
        //2、获得解码器上下文
        AVCodecContext *codecContext = avcodec_alloc_context3(dec);
        if (codecContext == NULL) {
            LOGE("创建解码上下文失败:%s", av_err2str(ret));
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            return;
        }
        //3、设置上下文内的一些参数 (codecContext->width)
//        codecContext->width = codecpar->width;
//        codecContext->height = codecpar->height;
        ret = avcodec_parameters_to_context(codecContext, codecpar);
        //失败
        if (ret < 0) {
            LOGE("设置解码上下文参数失败:%s", av_err2str(ret));
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            return;
        }
        // 4、打开解码器
        ret = avcodec_open2(codecContext, dec, 0);
        if (ret != 0) {
            LOGE("打开解码器失败:%s", av_err2str(ret));
            javaCallHelper->OnError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            return;
        }
        //音频
        if (codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            //0
            audioChannel = new AudioChannel(i, codecContext, avRational);
            if (videoChannel && audioChannel) {
                videoChannel->setAudioChannel(audioChannel);
            }
        } else if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            //1
            videoChannel = new VideoChannel(i, codecContext, avRational);
            if (videoChannel && audioChannel) {
                videoChannel->setAudioChannel(audioChannel);
            }
            videoChannel->setRenderFrameCallback(callback);
        }
    }

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
    if (videoChannel) {
        videoChannel->play();
    }
    if (audioChannel) {
        audioChannel->play();
    }
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
        ret = av_read_frame(formatContext, avPacket);
        if (ret == 0) {//表示成功
            //判断当前数据是音频还是视频？
            if (audioChannel && audioChannel->index == avPacket->stream_index) {//表示视频
                //将音频数据放到
                audioChannel->packets.push(avPacket);
            } else if (videoChannel && videoChannel->index == avPacket->stream_index) {
                //将读取到的流放到队列中取
                videoChannel->packets.push(avPacket);
            }
        } else if (ret == AVERROR_EOF) {//end of file;读取完成还没有播放完成

        }
    }
}

void DNFFmpeg::setRenderFrameCallback(RenderFrameCallback callback) {
    this->callback = callback;
}

void DNFFmpeg::stop() {
    isPlaying = 0;
    if (audioChannel) {
        audioChannel->stop();

    }
    if (videoChannel) {
        videoChannel->stop();
    }
}


DNFFmpeg::~DNFFmpeg() {
    /**
     * delete dataSource;
       dataSource = 0;
     */
    DELETE(dataSource);
    DELETE(javaCallHelper);
    DELETE(formatContext);
    DELETE(videoChannel);
    DELETE(audioChannel);


    pthread_t pid;
    int isPlaying = 0;
    pthread_t start_pid;
    RenderFrameCallback callback;
}
