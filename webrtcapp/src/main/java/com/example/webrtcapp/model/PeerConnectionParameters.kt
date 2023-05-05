package com.example.webrtcapp.model

class PeerConnectionParameters @JvmOverloads constructor(
    val videoCallEnabled: Boolean, //        回拨的意思
    val loopback: Boolean,
    val tracing: Boolean,
    val videoWidth: Int,
    val videoHeight: Int, //        帧率
    val videoFps: Int, //        比特率   60kb
    val videoMaxBitrate: Int, //视频编码
    val videoCodec: String,
    //硬编码
    val videoCodecHwAcceleration: Boolean,
    val videoFlexfecEnabled: Boolean,
    val audioStartBitrate: Int,
    val audioCodec: String,
    val noAudioProcessing: Boolean,
    val aecDump: Boolean,
    val useOpenSLES: Boolean,
    val disableBuiltInAEC: Boolean,
    val disableBuiltInAGC: Boolean,
    val disableBuiltInNS: Boolean,
    val enableLevelControl: Boolean,
    dataChannelParameters: DataChannelParameters? = null
) {
    private val dataChannelParameters: DataChannelParameters?

    init {
        this.dataChannelParameters = dataChannelParameters
    }

    companion object {
        fun createConnectDefaultParam(): PeerConnectionParameters {
            return PeerConnectionParameters(
                true, false,
                false, 0, 0, 0,
                0, "VP8",
                true,
                false,
                0, "OPUS",
                false,
                false,
                false,
                false,
                false,
                false,
                false
            )
        }
    }
}
