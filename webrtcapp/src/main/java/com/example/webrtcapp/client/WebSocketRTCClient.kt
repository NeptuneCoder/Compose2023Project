package com.example.webrtcapp.client

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.example.webrtcapp.iface.SignalingEvents
import com.example.webrtcapp.model.RoomConnectionParameters
import com.example.webrtcapp.model.SignalingParameters
import com.example.webrtcapp.net.AsyncHttpURLConnection
import com.example.webrtcapp.net.AsyncHttpURLConnection.AsyncHttpEvents
import com.example.webrtcapp.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription


class WebSocketRTCClient(events: SignalingEvents) : WebSocketChannelClient.WebSocketChannelEvents,
    RoomParametersFetcher.RoomParametersFetcherEvents {
    private val TAG = "WSRTCClient"
    private val ROOM_JOIN = "join"
    private val ROOM_MESSAGE = "message"
    private val ROOM_LEAVE = "leave"



    private enum class ConnectionState {
        NEW, CONNECTED, CLOSED, ERROR
    }

    private enum class MessageType {
        MESSAGE, LEAVE
    }

    private var handler: Handler? = null
    private var initiator = false
    private var events: SignalingEvents? = null
    private var roomState: ConnectionState? = null
    private lateinit var connectionParameters: RoomConnectionParameters
    private var messageUrl: String? = null
    private var leaveUrl: String? = null

    init {
        this.events = events
        roomState = ConnectionState.NEW
        val handlerThread = HandlerThread(this::class.qualifiedName)
        handlerThread.start()
        handler = Handler(handlerThread.looper)

    }

    lateinit var wsClient: WebSocketChannelClient
    fun connect2rRoom(roomParams: RoomConnectionParameters) {
        this.connectionParameters = roomParams

        handler?.post {
            wsClient = WebSocketChannelClient(handler!!, this)
            val roomParamsFetcher =
                RoomParametersFetcher(roomParams.roomUrl + "/join/" + roomParams.roomId, null, this)
            roomParamsFetcher.makeRequest()
        }


    }

    /*******************************这是创建WebSocketChannelClient 给的回调信息***********************************/
    //A 向 B发送视频请求
    //B进入以后，服务器通过socket给A回调信息
    override fun onWebSocketMessage(message: String?) {
        //注册
        if (wsClient.state !== WebSocketChannelClient.WebSocketConnectionState.REGISTERED) {
            Log.e(TAG, "Got WebSocket message in non registered state.")
            return
        }

        try {
            val json = JSONObject(message)
            val msgText = json.getString("msg")
            if (msgText.isNotEmpty()) {
                Log.i(TAG, "onWebSocketMessage: json1  $msgText")
                val json1 = JSONObject(msgText)
                val type = json1.getString("type")
                if (type == "candidate") {
                    Log.i(TAG, "onWebSocketMessage:------lable值 ----> " + json1.getInt("label"))
                    val iceCandidate = IceCandidate(
                        json1.getString("id"), json1.getInt("label"),
                        json1.getString("candidate")
                    )
                    events?.onRemoteIceCandidate(iceCandidate)
                } else if (type == "remove-candidates") {
                    val candidateArray = json1.getJSONArray("candidates")
                    val candidates = arrayOfNulls<IceCandidate>(candidateArray.length())
                    for (i in 0 until candidateArray.length()) {
                        val iceJson = candidateArray.getJSONObject(i)
                        val iceCandidate = IceCandidate(
                            iceJson.getString("id"),
                            iceJson.getInt("lable"),
                            iceJson.getString("candidate")
                        )
                        candidates[i] = iceCandidate
                    }
                    events?.onRemoteIceCandidatesRemoved(candidates)
                } else if (type == "answer") {
//
                    if (initiator) {
//                        主动发送视频       并且对方同意了    sdp 对方
                        val sdp = SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(type), json1.getString("sdp")
                        )
                        events?.onRemoteDescription(sdp)
                    }
                } else if (type == "offer") {
                    if (!initiator) {
                        val sdp = SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(type), json1.getString("sdp")
                        )
                        events?.onRemoteDescription(sdp)
                    }
                } else if (type == "bye") {
                    events?.onChannelClose()
                }
            }
        } catch (e: JSONException) {
            Log.i(TAG, "onWebSocketMessage: $e")
        }
    }

    override fun onWebSocketClose() {

    }

    override fun onWebSocketError(description: String?) {

    }

    /***************************RoomParametersFetcher回调***************************************/
    override fun onSignalingParametersReady(params: SignalingParameters?) {
        messageUrl =
            Utils.getMessageUrl(
                connectionParameters,
                params!!
            )
        handler?.post {
            initiator = params.initiator
            roomState = ConnectionState.CONNECTED
            events?.onConnectedToRoom(params)
            //注册长连接监听被邀请方上线后的通知
            wsClient.connect(params.wssUrl, params.wssPostUrl)
            wsClient.register(roomID = connectionParameters.roomId, params.clientId)
        }
    }

    override fun onSignalingParametersError(description: String?) {

    }

    fun sendOfferSdp(sdp: SessionDescription?) {
        handler?.post {
            if (roomState != ConnectionState.CONNECTED) {
                return@post
            }
            val param = JSONObject()
            param.put("sdp", sdp?.description)
            param.put("type", "offer")
            sendPostMessage(MessageType.MESSAGE, "$messageUrl", param.toString())
        }
    }

    private fun sendPostMessage(
        messageType: MessageType,
        messageUrl: String,
        message: String
    ) {
        var logInfo: String = messageUrl
        if (message != null) {
            logInfo += ". Message: $message"
        }
        Log.i("tag", "logInfo == $logInfo")
        val httpConnection =
            AsyncHttpURLConnection("POST", messageUrl, message, object : AsyncHttpEvents {
                override fun onHttpError(errorMessage: String) {}
                override fun onHttpComplete(response: String) {
                    if (messageType === MessageType.MESSAGE) {
                        try {
                            val roomJson = JSONObject(response)
                            val result = roomJson.getString("result")
                            if (result != "SUCCESS") {
                            }
                        } catch (e: JSONException) {
                        }
                    }
                }
            })
        httpConnection.send()
    }

    fun sendAnswerSdp(sdp: SessionDescription) {
        handler?.post {
            val json = JSONObject()
            json.put("sdp", sdp.description)
            json.put("type", "answer")
            wsClient.send(json.toString())
        }

    }

    /**
     * 发送candidate给服务器
     */
    fun sendLocalIceCandidate(candidate: IceCandidate) {
        handler?.post {
            val json = JSONObject()
            json.put("type", "candidate")
            json.put("label", candidate.sdpMLineIndex)
            json.put("id", candidate.sdpMid)
            json.put("candidate", candidate.sdp)
            if (initiator) {
                if (roomState != ConnectionState.CONNECTED) {
                    return@post
                }
                sendPostMessage(MessageType.MESSAGE, "$messageUrl", json.toString())
                if (connectionParameters.loopback) {
                    events?.onRemoteIceCandidate(candidate)
                }
            } else {
                wsClient.send(json.toString())
            }
        }

    }
    /******************************************************************/
}