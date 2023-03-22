//
// Created by yanghai on 2023/3/21.
//


#include "VideoChannel.h"




VideoChannel::VideoChannel() {
    pthread_mutex_init(&mutex, 0);
};

VideoChannel::~VideoChannel() {
    //销毁所
    pthread_mutex_destroy(&mutex);

    if (videoCodec) {//如果已经打开过则关闭重新打开
        x264_encoder_close(videoCodec);
        videoCodec = 0;
    }
    if (pic_in) {
        x264_picture_clean(pic_in);
        DELETE(pic_in);
    }
};

void VideoChannel::setVideoEncInfo(int width, int height, int fps, int bitrate) {
    pthread_mutex_lock(&mutex);
    //初始化编码器
    this->width = width;
    this->height = height;
    this->fps = fps;
    this->bitrate = bitrate;
    ySize = width * height;
    uvSize = ySize / 4;
    if (videoCodec) {//如果已经打开过则关闭重新打开
        x264_encoder_close(videoCodec);
        videoCodec = 0;
    }
    if (pic_in) {
        x264_picture_clean(pic_in);
        DELETE(pic_in);
    }
    //打开编码器
    x264_param_t param;
    /**
     * zerolatency：编码图片时适合的一些场景，zerolatency表示无延迟编码
     */
    x264_param_default_preset(&param, "ultrafast", "zerolatency");
    param.i_level_idc = 32;//在维基百科上输入H.264可以查到；表示码率越高
    param.i_csp = X264_CSP_I420;//表示输入的格式
    param.i_width = width;
    param.i_height = height;
    //无b帧；根据i，b，p编码格式中，b帧表示只有双向预测帧
    param.i_bframe = 0;
    //控制码率，码率越大数据量越大；  X264_RC_ABR:表示平均码率，CRF:恒定码率；COP:恒定质量
    param.rc.i_rc_method = X264_RC_ABR;
    //瞬时最大码率
    param.rc.i_bitrate = bitrate / 1000;

    //瞬时最大码率
    param.rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2;
    //设置了i_vbv_max_bitrate必须设置此参数，码率控制区大小,单位kbps
    param.rc.i_vbv_buffer_size = bitrate / 1000;

    //帧率
    param.i_fps_num = fps;
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
//    param.pf_log = x264_log_default2;
    //用fps而不是时间戳来计算帧间距离
    param.b_vfr_input = 0;


    // 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
    // sps/pps的作用是告诉播放器如何解码后续的图像;
    param.b_repeat_headers = 1;

    //帧距离(关键帧)  2s一个关键帧；任何一个画面都可以编译成关键帧
    param.i_keyint_max = fps * 2;

    //多线程
    param.i_threads = 1;

    x264_param_apply_profile(&param, "baseline");
    //打开编码器
    videoCodec = x264_encoder_open(&param);
    pic_in = new x264_picture_t;
    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);

    pthread_mutex_unlock(&mutex);
};


void VideoChannel::encodeData(int8_t *data) {
    pthread_mutex_lock(&mutex);
    memcpy(pic_in->img.plane[0], data, ySize);
    //数据有nv21转成1420
    for (int i = 0; i < uvSize; ++i) {
        *(pic_in->img.plane[1] + i) = *(data + ySize + i * 2 + 1);
        *(pic_in->img.plane[2] + i) = *(data + ySize + i * 2);
    }
    //编码出来的数据；帧的数组
    x264_nal_t *pp_nal;
    //编码出来的有几个数据;几个帧的数据
    int pi_nal;

    x264_picture_t pic_out;
    //编码
    x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_in, &pic_out);
    //如果是关键帧， 3
    int sps_len;
    int pps_len;
    uint8_t sps[100];
    uint8_t pps[100];
    for (int i = 0; i < pi_nal; ++i) {
        if (pp_nal[i].i_type == NAL_SPS) {
            sps_len = pp_nal[i].i_payload -
                      4;//为什么要-4？一段包含了N个图像的H。264裸数据，没个NAL之间由 00 00 00 01 或者 00 00 01进行分割
            memcpy(sps, pp_nal[i].p_payload + 4, sps_len);
        } else if (pp_nal[i].i_type == NAL_PPS) {
            pps_len = pp_nal[i].i_payload -
                      4;//为什么要-4？一段包含了N个图像的H。264裸数据，没个NAL之间由 00 00 00 01 或者 00 00 01进行分割
            memcpy(pps, pp_nal[i].p_payload + 4, pps_len);
            //pps肯定跟着 sps的
            sendSpsPps(sps, pps, sps_len, pps_len);
        } else {
            sendFrame(pp_nal[i].i_type, pp_nal[i].p_payload, pp_nal[i].i_payload);
        }

    }
    pthread_mutex_unlock(&mutex);
}

void VideoChannel::sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    RTMPPacket *packet = new RTMPPacket;
    //数据总长度；根据课件中视频包+视频解码序列表
    int bodysize = 13 + sps_len + 3 + pps_len;
    RTMPPacket_Alloc(packet, bodysize);
    int i = 0;
    //固定头
    packet->m_body[i++] = 0x17;
    //类型
    packet->m_body[i++] = 0x00;
    //composition time 0x000000
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;

    //版本
    packet->m_body[i++] = 0x01;
    //编码规格
    packet->m_body[i++] = sps[1];
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];
    packet->m_body[i++] = 0xFF;

    //整个sps
    packet->m_body[i++] = 0xE1;
    //sps长度
    packet->m_body[i++] = (sps_len >> 8) & 0xff;
    packet->m_body[i++] = sps_len & 0xff;
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;

    //pps
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (pps_len >> 8) & 0xff;
    packet->m_body[i++] = (pps_len) & 0xff;
    memcpy(&packet->m_body[i], pps, pps_len);


    //视频
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodysize;
    //随意分配一个管道（尽量避开rtmp.c中使用的）
    packet->m_nChannel = 10;
    //sps pps没有时间戳
    packet->m_nTimeStamp = 0;
    //不使用绝对时间
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    videoCallback(packet);
}

void VideoChannel::setRTMPPacketCallback(RTMPPacketCallback callback) {
    this->videoCallback = callback;
}

void VideoChannel::sendFrame(int type, uint8_t *p_payload, int payload) {
    //去掉 00 00 00 01 / 00 00 01
    if (p_payload[2] == 0x00) {//表示以4分割
        payload -= 4;
        p_payload += 4;
    } else if (p_payload[2] == 0x01) {//否者以3分割
        payload -= 3;
        p_payload += 3;
    }
    RTMPPacket *packet = new RTMPPacket;
    int bodysize = 9 + payload;
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet);
//    int type = payload[0] & 0x1f;
    packet->m_body[0] = 0x27;
    //关键帧
    if (type == NAL_SLICE_IDR) {//关键帧
        LOGE("关键帧");
        packet->m_body[0] = 0x17;
    }
    //类型
    packet->m_body[1] = 0x01;
    //时间戳
    packet->m_body[2] = 0x00;
    packet->m_body[3] = 0x00;
    packet->m_body[4] = 0x00;
    //数据长度 int 4个字节 相当于把int转成4个字节的byte数组
    packet->m_body[5] = (payload >> 24) & 0xff;
    packet->m_body[6] = (payload >> 16) & 0xff;
    packet->m_body[7] = (payload >> 8) & 0xff;
    packet->m_body[8] = (payload) & 0xff;

    //图片数据
    memcpy(&packet->m_body[9], p_payload, payload);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodysize;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x10;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    videoCallback(packet);
}

