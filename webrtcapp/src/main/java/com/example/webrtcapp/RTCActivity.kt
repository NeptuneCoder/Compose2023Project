package com.example.webrtcapp

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.webrtcapp.client.PeerConnectionClient
import com.example.webrtcapp.client.WebSocketRTCClient
import com.example.webrtcapp.databinding.ActivityRtcBinding

import com.example.webrtcapp.iface.PeerConnectionEvents
import com.example.webrtcapp.iface.SignalingEvents
import com.example.webrtcapp.model.PeerConnectionParameters
import com.example.webrtcapp.model.RoomConnectionParameters
import com.example.webrtcapp.model.SignalingParameters
import com.example.webrtcapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.*
import  org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL;
import  org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT;

@AndroidEntryPoint
class RTCActivity : AppCompatActivity(), PeerConnectionEvents, SignalingEvents {
    lateinit var binding: ActivityRtcBinding
    val mainViewModel by viewModels<RTCViewModel>()
    var roomId: String = ""
    lateinit var params: SignalingParameters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rtc)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        roomId = "${intent.getStringExtra("roomId")}"
        initWebrtc()
        connectRoom()


    }


    lateinit var webSocketRTCClient: WebSocketRTCClient
    lateinit var roomParams: RoomConnectionParameters
    private fun connectRoom() {

        Log.i("roomId", "roomId == $roomId")
        roomParams = RoomConnectionParameters("https://apprtc.webrtcserver.cn", roomId, false)
        webSocketRTCClient = WebSocketRTCClient(this)
        webSocketRTCClient.connect2rRoom(roomParams)
    }

    lateinit var peerConnectDefaultParams: PeerConnectionParameters
    lateinit var eglBase: EglBase
    private fun initWebrtc() {
        eglBase = EglBase.create()
        binding.localVideoView.init(eglBase.eglBaseContext, null);
        binding.localVideoView.setZOrderMediaOverlay(true)//悬浮顶端
        binding.localVideoView.setEnableHardwareScaler(true)//硬件加速
        binding.remoteVideoView.init(eglBase.eglBaseContext, null)
        binding.remoteVideoView.setEnableHardwareScaler(true)//硬件加速
        binding.remoteVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        binding.remoteVideoView.setMirror(true)//加速读取速度
        //设置连接参数
        peerConnectDefaultParams = PeerConnectionParameters.createConnectDefaultParam()
        PeerConnectionClient.createPeerConnectionFactory(
            this,
            peerConnectDefaultParams,
            this,
            binding
        )

    }

    /**********************创建链接房间后的回调*************************/
    /**
     * 第一次调服务器接口返回的参数
     */
    override fun onConnectedToRoom(params: SignalingParameters) {
        this.params = params
        val videoCapturer = Utils.createVideoCaptuer(this)
        //创建本地摄像头
        PeerConnectionClient.createPeerConnection(
            eglBase.eglBaseContext,
            binding.localVideoView,
            binding.remoteVideoView,
            videoCapturer!!,
            params
        )
        //发送offer给被邀请端
        if (params.initiator) {//如果是邀请人
            PeerConnectionClient.createOffer()
        } else {//被邀请
            if (params.offerSdp != null) {
                //说明是被邀请，同时对方已经将对方的sdp发送给我被邀请方
                PeerConnectionClient.setRemoteDescription(params.offerSdp)
                //TODO  获得自己的sdp

                //将自己的sdp发送给邀请方
                PeerConnectionClient.createAnswer()
            }
        }
    }

    override fun onRemoteDescription(sdp: SessionDescription) {
        runOnUiThread {
            //设置远端sdp
            PeerConnectionClient.setRemoteDescription(sdp)

            //signalingParameters.initiator=true  主动发送视频
            if (!params.initiator) {
                PeerConnectionClient.createAnswer()
            }
        }
    }

    override fun onRemoteIceCandidate(candidate: IceCandidate) {
        runOnUiThread {
            PeerConnectionClient.addRemoteIceCandidate(candidate)
        }
    }

    override fun onRemoteIceCandidatesRemoved(candidates: Array<IceCandidate?>) {
//        TODO("Not yet implemented")
    }

    override fun onChannelClose() {
//        TODO("Not yet implemented")
    }

    override fun onChannelError(description: String) {
//        TODO("Not yet implemented")
    }


    /**********************创建链接房间后的回调*************************/


    /***************************链接后的回调*******************************************/
    override fun onLocalDescription(sdp: SessionDescription) {
        if (params.initiator) {
            //邀请方将自己的sdp发送给服务器
            webSocketRTCClient.sendOfferSdp(sdp)
        } else {//被叫
            webSocketRTCClient.sendAnswerSdp(sdp)
        }
    }

    override fun onIceCandidate(candidate: IceCandidate?) {
        if (candidate != null) {
            webSocketRTCClient.sendLocalIceCandidate(candidate)
        }

    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>?) {

    }

    override fun onIceConnected() {
        runOnUiThread {
            updateVideoView()
        }
    }

    private fun updateVideoView() {
        binding.remoteVideoLayout.setPosition(0, 0, 100, 100)
        binding.remoteVideoView.setScalingType(SCALE_ASPECT_FILL)
        binding.remoteVideoView.setMirror(false)
        binding.localVideoLayout.setPosition(
            72, 72, 25, 25
        )
        binding.localVideoView.setScalingType(SCALE_ASPECT_FIT)
        binding.localVideoView.setMirror(true)
        binding.localVideoLayout.requestLayout()
        binding.remoteVideoLayout.requestLayout()
    }

    override fun onIceDisconnected() {

    }

    override fun onPeerConnectionClosed() {
    }

    override fun onPeerConnectionStatsReady(reports: Array<StatsReport?>?) {

    }

    override fun onPeerConnectionError(description: String?) {

    }


    /***************************链接后的回调*******************************************/

}