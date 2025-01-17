package com.example.webrtcapp.iface

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.StatsReport

interface PeerConnectionEvents {
    /**
     * Callback fired once local SDP is created and set.
     * 网络路径   客户端----》服务器
     */
    fun onLocalDescription(sdp: SessionDescription)

    /**
     * Callback fired once local Ice candidate is generated.
     */
    fun onIceCandidate(candidate: IceCandidate?)

    /**
     * Callback fired once local ICE candidates are removed.
     */
    fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>?)

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED).
     */
    fun onIceConnected()

    /**
     * Callback fired once connection is closed (IceConnectionState is
     * DISCONNECTED).
     */
    fun onIceDisconnected()

    /**
     * Callback fired once peer connection is closed.
     */
    fun onPeerConnectionClosed()

    /**
     * Callback fired once peer connection statistics is ready.
     */
    fun onPeerConnectionStatsReady(reports: Array<StatsReport?>?)

    /**
     * Callback fired once peer connection error happened.
     */
    fun onPeerConnectionError(description: String?)
}
