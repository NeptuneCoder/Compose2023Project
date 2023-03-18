//
// Created by yanghai on 2023/3/11.
//
#include "AudioChannel.h"
#include "macro.h"


AudioChannel::AudioChannel(int index, AVCodecContext *context) : BaseChannel(index, context) {
    //44100 * 2 因为采用的时16为采样位
    //44100 * 2 * 2 ：表示双声道
    out_simple_rate = 44100;
    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    out_samplesize = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    data = static_cast<uint8_t *>(malloc(out_simple_rate * 2 * 2));
    memset(data, 0, sizeof(data));

};


AudioChannel::~AudioChannel() {
    if (data) {
        free(data);
        data = 0;
    }

};

void *task_decode_audio(void *args) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->decodeAudio();
    return 0;
}

void *task_play_audio(void *args) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->playAudio();
    return 0;
}


void AudioChannel::decodeAudio() {
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

//返回获取的pcm数据大小
int AudioChannel::getPcm() {
    int data_size = 0;
    AVFrame *frame;
    int ret = avFrames.pop(frame);
    if (!isPlaying) {
        if (ret) {
            releaseAvFrame(&frame);
        }
        return data_size;
    }
    //48000HZ 8位 =》 44100 16位
    //重采样:有可能采样的数据大小和播放设置的采样率不一致时需要进行重采样。
    // 假设我们输入了10个数据 ，swrContext转码器 这一次处理了8个数据
    // 那么如果不加delays(上次没处理完的数据) ,就会造成积压；如果不处理就会越积越多，导致崩栈。
    //从采样的数据
    int64_t delays = swr_get_delay(swrContext, frame->sample_rate);//就是为了将之前没有处理完的数据继续处理
    // 将 nb_samples 个数据 由 sample_rate采样率转成 44100 后 返回多少个数据
    // 10  个 48000 = max_samples 个 44100
    // AV_ROUND_UP : 向上取整 1.1 = 2
    //是将源数据计算出符合目标输出设备需要的数据。
    int64_t max_samples = av_rescale_rnd(delays + frame->nb_samples, //
                                         out_simple_rate,
                                         frame->sample_rate,
                                         AV_ROUND_UP);//向上取整
    //上下文+输出缓冲区+输出缓冲区能接受的最大数据量+输入数据+输入数据个数
    //返回 每一个声道的真正输出的数据;
    int samples = swr_convert(swrContext, &data, max_samples, (const uint8_t **) frame->data,
                              frame->nb_samples);
    //获得   samples 个   * 2 声道 * 2字节（16位）
    LOGE("out_samplesize == %d  out_channels == %d", out_samplesize, out_channels);
    data_size = samples * out_samplesize * out_channels;
    return data_size;
}

//回调函数
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(context);
    //获得pcm 数据 多少个字节 data
    int dataSize = audioChannel->getPcm();
    if (dataSize > 0) {
        // 接收16位数据
        (*bq)->Enqueue(bq, audioChannel->data, dataSize);
    }
}

void AudioChannel::playAudio() {
//
////type un
//    SLresult result;
//
//    // create engine
//    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
//    if (SL_RESULT_SUCCESS != result) {
//        return;
//    }
//
//
//    // realize(了解) the engine
//    //初始化引擎；init
//    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
//    if (SL_RESULT_SUCCESS != result) {
//        return;
//    }
//
//
//    // get the engine interface, which is needed in order to create other objects
//    //获取引擎接口
//    result =
//            (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
//    assert(SL_RESULT_SUCCESS == result);
//    (void) result;
//
//    // create output mix, with environmental reverb specified as a non-required
//    // interface
//    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
//    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
//
//    result = (*engineInterface)
//            ->CreateOutputMix(engineInterface, &outputMixObject, 1, ids, req);
//    if (SL_RESULT_SUCCESS != result) {
//        return;
//    }
//
//
//    // realize the output mix；初始化混音器
//    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
//    if (SL_RESULT_SUCCESS != result) {
//        return;
//    }
//
//
///**
//     * 3、创建播放器
//     */
//    //3.1 配置输入声音信息
//    //创建buffer缓冲类型的队列 2个队列
//    SLDataLocator_AndroidSimpleBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
//                                                            2};
//    //pcm数据格式
//    //pcm+2(双声道)+44100(采样率)+ 16(采样位)+16(数据的大小)+LEFT|RIGHT(双声道)+小端数据
//    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1, SL_PCMSAMPLEFORMAT_FIXED_16,
//                            SL_PCMSAMPLEFORMAT_FIXED_16,
//                            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
//                            SL_BYTEORDER_LITTLEENDIAN};
//
//    //数据源 将上述配置信息放到这个数据源中
//    SLDataSource slDataSource = {&android_queue, &pcm};
//
//    //3.2  配置音轨(输出)
//    //设置混音器
//    //在FFmpeg中，SLDataLocator_OutputMix是一个结构体，用于指定一个输出混音器的地址。
//    // 而SL_DATALOCATOR_OUTPUTMIX是一个常量，用于表示数据定位器的类型是输出混音器。
//    // 在SLDataLocator_OutputMix结构体中，第二个参数是一个对象，用于表示输出混音器的实例。
//    //因此，SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject}
//    // 这行代码的作用是将一个输出混音器对象（outputMixObject）封装到一个SLDataLocator_OutputMix结构体中，
//    // 从而将该输出混音器的地址指定为outputMixObject。这样，FFmpeg就可以将音频数据输出到该输出混音器中。
//    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
//    //audioSnk就是音轨
//    SLDataSink audioSnk = {&outputMix, NULL};
//    //需要的接口  操作队列的接口
////    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
////    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
//    //3.3 创建播放器
//
//    (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &slDataSource,
//                                          &audioSnk, 1,
//                                          ids, req);
//    //初始化播放器
//    (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
//
//    //得到接口后调用  获取Player接口
//    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);
//
//
//    /**
//     * 4、设置播放回调函数
//     */
//    //获取播放器队列接口
//    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
//                                    &bqPlayerBufferQueueInterface);
//    //设置回调
//    (*bqPlayerBufferQueueInterface)->RegisterCallback(bqPlayerBufferQueueInterface,
//                                                      bqPlayerCallback, this);
//    /**
//     * 5、设置播放状态
//     */
//    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);
//    /**
//     * 6、手动激活一下这个回调
//     */
//    bqPlayerCallback(bqPlayerBufferQueueInterface, this);


/**
     * 1、创建引擎并获取引擎接口
     */
    SLresult result;
    // 1.1 创建引擎 SLObjectItf engineObject
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 1.2 初始化引擎  init
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 1.3 获取引擎接口SLEngineItf engineInterface
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE,
                                           &engineInterface);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }

    /**
     * 2、设置混音器
     */
    // 2.1 创建混音器SLObjectItf outputMixObject
    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0,
                                                 0, 0);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 2.2 初始化混音器outputMixObject
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }

    /**
     * 3、创建播放器
     */
    //3.1 配置输入声音信息
    //创建buffer缓冲类型的队列 2个队列
    SLDataLocator_AndroidSimpleBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                                                            2};
    //pcm数据格式
    //pcm+2(双声道)+44100(采样率)+ 16(采样位)+16(数据的大小)+LEFT|RIGHT(双声道)+小端数据
    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1, SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                            SL_BYTEORDER_LITTLEENDIAN};

    //数据源 将上述配置信息放到这个数据源中
    SLDataSource slDataSource = {&android_queue, &pcm};

    //    //3.2  配置音轨(输出)
    //设置混音器
    //在FFmpeg中，SLDataLocator_OutputMix是一个结构体，用于指定一个输出混音器的地址。
    // 而SL_DATALOCATOR_OUTPUTMIX是一个常量，用于表示数据定位器的类型是输出混音器。
    // 在SLDataLocator_OutputMix结构体中，第二个参数是一个对象，用于表示输出混音器的实例。
    //因此，SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject}
    // 这行代码的作用是将一个输出混音器对象（outputMixObject）封装到一个SLDataLocator_OutputMix结构体中，
    // 从而将该输出混音器的地址指定为outputMixObject。这样，FFmpeg就可以将音频数据输出到该输出混音器中。
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};
    //需要的接口  操作队列的接口
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    //3.3 创建播放器
    (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &slDataSource,
                                          &audioSnk, 1,
                                          ids, req);
    //初始化播放器
    (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);

    //得到接口后调用  获取Player接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);


    /**
     * 4、设置播放回调函数
     */
    //获取播放器队列接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
                                    &bqPlayerBufferQueueInterface);
    //设置回调
    (*bqPlayerBufferQueueInterface)->RegisterCallback(bqPlayerBufferQueueInterface,
                                                      bqPlayerCallback, this);
    /**
     * 5、设置播放状态
     */
    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);
    /**
     * 6、手动激活一下这个回调
     */
    bqPlayerCallback(bqPlayerBufferQueueInterface, this);


}

void AudioChannel::play() {
    isPlaying = 1;
    packets.setWork(1);
    avFrames.setWork(1);
    //0+输出声道+输出采样位+输出采样率+  输入的3个参数
    swrContext = swr_alloc_set_opts(0, AV_CH_LAYOUT_STEREO, //输出声道：双声道
                                    AV_SAMPLE_FMT_S16,//输出采样位
                                    out_simple_rate,//输出采样率
                                    codecContext->channel_layout,//
                                    codecContext->sample_fmt,//
                                    codecContext->sample_rate,//
                                    0,
                                    0);
    //初始化
    swr_init(swrContext);
    pthread_create(&decode_audio_pid, 0, task_decode_audio, this);
    pthread_create(&play_audio_pid, 0, task_play_audio, this);

}