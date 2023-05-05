package com.example.webrtcapp.model

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection.IceServer
import org.webrtc.SessionDescription


data class SignalingParameters(
    val iceServers: List<IceServer>,
    val initiator: Boolean,//邀请人
    val clientId: String,
    val wssUrl: String,
    val wssPostUrl: String,
    val offerSdp: SessionDescription?,
    val iceCandidates: List<IceCandidate>?
)