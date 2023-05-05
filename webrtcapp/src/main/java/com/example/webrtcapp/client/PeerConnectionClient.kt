package com.example.webrtcapp.client

import android.content.Context
import android.util.Log
import com.example.webrtcapp.databinding.ActivityRtcBinding
import com.example.webrtcapp.iface.PeerConnectionEvents
import com.example.webrtcapp.model.PeerConnectionParameters
import com.example.webrtcapp.model.SignalingParameters
import com.example.webrtcapp.utils.Utils
import org.webrtc.*
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


object PeerConnectionClient {
    private var executor: ScheduledExecutorService? = null
    lateinit var contextRef: WeakReference<Context>

    //用于监听对方是否接收视频通话请求
    private val pcObserver: PCObserver = PCObserver()
    private val sdpObserver: SDPObserver = SDPObserver()
    private lateinit var pcConstraints: MediaConstraints

    private var videoWidth = 0
    private var videoHeight = 0
    private var videoFps = 0
    var audioConstraints: MediaConstraints? = null
    private lateinit var sdpMediaConstraints: MediaConstraints
    private lateinit var mediaStream: MediaStream
    private lateinit var mainActivityEvents: PeerConnectionEvents
    private val cacheCandidate = mutableListOf<IceCandidate>()

    /**
     * 邀请方的sdp
     */
    var localSdp: SessionDescription? = null

    //    本地的视频源
    private lateinit var localVideoTrack: VideoTrack

    //远程的视频源
    private var remoteVideoTrack: VideoTrack? = null

    private lateinit var peerConnection: PeerConnection
    private lateinit var factory: PeerConnectionFactory
    private var options: PeerConnectionFactory.Options = PeerConnectionFactory.Options()
    private var localVideoSender: RtpSender? = null

    init {
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    lateinit var binding: ActivityRtcBinding
    fun createPeerConnectionFactory(
        context: Context,
        peerConnectionParams: PeerConnectionParameters,//参数
        events: PeerConnectionEvents,//回调
        binding: ActivityRtcBinding
    ) {
        mainActivityEvents = events
        this.binding = binding
        contextRef = WeakReference<Context>(context)
        //创建链接通道并初始化
        PeerConnectionFactory.initializeInternalTracer()
        PeerConnectionFactory.initializeFieldTrials("")
        //设置对方断开后自动断开
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false)
        //设置自动操作
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false)
        //设置 Web RTC 的噪声抑制器
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)

        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true)
        //初始化工厂
        factory = PeerConnectionFactory(options)
    }

    /**
     * 创建连接
     */
    fun createPeerConnection(
        renderEGLContext: EglBase.Context,
        localview: SurfaceViewRenderer,
        remoteView: SurfaceViewRenderer,
        videoCapturer: VideoCapturer,
        signalingParameters: SignalingParameters
    ) {
        executor!!.execute {
            Log.i("david", "run: ----------------------------------->2")
            pcConstraints = MediaConstraints()
            pcConstraints.optional.add(
                MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true")
            )
            //1280  *720
            videoWidth = 1280
            videoHeight = 720
            videoFps = 30
            // Create audio constraints.
            audioConstraints = MediaConstraints()
            sdpMediaConstraints = MediaConstraints()
            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
            )
            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
            )
            Log.i("david", " 2  PeerConnectionFactory 初始化是否完成   $factory")
            factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext)
            val rtcConfig = RTCConfiguration(signalingParameters.iceServers)
            // TCP candidates are only useful when connecting to a server that supports
            // ICE-TCP.
            rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            rtcConfig.continualGatheringPolicy =
                PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            // Use ECDSA encryption.
            rtcConfig.keyType = PeerConnection.KeyType.ECDSA
            //不是阻塞   peerConnection--->
            peerConnection = factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver)

//                声音    推送B
//                创建一个音频音源
            val audioSource = factory.createAudioSource(audioConstraints)
            //表示自带的话筒，如果是外置的话需要修改参数
            val audioTrack = factory.createAudioTrack("ARDAMSa0", audioSource)
            audioTrack.setEnabled(true)
            //开启一个视频流
            mediaStream = factory.createLocalMediaStream("ARDAMS")
            //               音源有了  推送B
            mediaStream.addTrack(audioTrack)


            //                创建一个视频源
            val videoSource = factory.createVideoSource(videoCapturer)
            //                预览的格式
            videoCapturer.startCapture(videoWidth, videoHeight, videoFps)
            localVideoTrack = factory.createVideoTrack("ARDAMSv0", videoSource)
            localVideoTrack.setEnabled(true)
            localVideoTrack.addRenderer(VideoRenderer(localview))
            //远端就能够看到 摄像头的画面
            mediaStream.addTrack(localVideoTrack)
            peerConnection.addStream(mediaStream)
            peerConnection.senders.forEach {
                if (it.track().kind().equals("video")) {
                    localVideoSender = it
                }
            }
            //
//                视频B端
        }
    }

    fun setRemoteDescription(description: SessionDescription) {
        executor?.execute {
            val sdpDescription = description.description
            val sessionDescription = SessionDescription(description.type, sdpDescription)
            peerConnection.setRemoteDescription(sdpObserver, sessionDescription)

        }
    }

    fun createAnswer() {
        executor?.execute {
            isInitiator = false
            peerConnection.createAnswer(sdpObserver, sdpMediaConstraints)

        }

    }

    var isInitiator = false
    fun createOffer() {
        executor?.execute {
            isInitiator = true
            //创建视频的条件
            peerConnection.createOffer(
                sdpObserver,
                sdpMediaConstraints
            )//发送一个offer ，如果创建成功则回调sdpObserver

        }

    }

    fun putAllIceCandidate() {
        cacheCandidate.forEach {
            peerConnection.addIceCandidate(it)
        }
        cacheCandidate.clear()
    }

    fun addRemoteIceCandidate(candidate: IceCandidate) {

        executor?.execute {
            cacheCandidate.add(candidate)
        }
    }


    /**
     * 监听connection创建过程
     */
    class SDPObserver : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription) {
            if (localSdp == null) {
                val description = Utils.preferCodec(p0.description, "VP8", false)//转换成VP8格式
                //创建本地的本地的sdp
                localSdp = SessionDescription(p0.type, description)
                executor?.execute {
                    if (peerConnection != null) {
                        peerConnection.setLocalDescription(sdpObserver, localSdp)
                    }
                }
            }

        }

        //设置成功了回调： peerConnection.setLocalDescription(sdpObserver, localSdp)
        override fun onSetSuccess() {
            //发送方把本地的sdp发送给信令服务器
            executor?.execute {
                if (peerConnection == null) return@execute
                if (isInitiator) {
                    if (peerConnection.remoteDescription == null) {
                        mainActivityEvents.onLocalDescription(localSdp!!)
                    } else {
                        putAllIceCandidate()
                    }

                } else if (peerConnection.localDescription != null) {//被叫
                    mainActivityEvents.onLocalDescription(localSdp!!)
                    putAllIceCandidate()
                }
            }
        }

        override fun onCreateFailure(p0: String?) {

        }

        override fun onSetFailure(p0: String?) {

        }

    }


    class PCObserver : PeerConnection.Observer {
        //方法 native 回调
        override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {}
        override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
            //当ICE完全交换状态会发生改变
            executor?.execute {
                if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                    mainActivityEvents.onIceConnected()//链接完成后修改界面
                }
            }
        }

        override fun onIceConnectionReceivingChange(b: Boolean) {}
        override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {}

        /**
         * 参生ice令牌
         */
        override fun onIceCandidate(iceCandidate: IceCandidate) {
            Log.i("TAG", "onIceCandidate === " + iceCandidate.sdp)
            executor?.execute {
                mainActivityEvents.onIceCandidate(iceCandidate)
            }
        }

        override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {}

        /**
         *  写入流;//最终显示的地方
         */
        override fun onAddStream(mediaStream: MediaStream) {
            executor?.execute {
                if (mediaStream.audioTracks.size > 1 || mediaStream.videoTracks.size > 1) {
                    return@execute
                }
                if (mediaStream.videoTracks.size == 1) {
                    remoteVideoTrack = mediaStream.videoTracks[0]
                    remoteVideoTrack?.setEnabled(true)
                    remoteVideoTrack?.addRenderer(VideoRenderer(binding.remoteVideoView))
                }
            }
        }

        override fun onRemoveStream(mediaStream: MediaStream) {}
        override fun onDataChannel(dataChannel: DataChannel) {}
        override fun onRenegotiationNeeded() {}
    }
}