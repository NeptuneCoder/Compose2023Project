/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.example.webrtcapp.client

import android.os.Handler
import android.util.Log
import com.example.webrtcapp.client.WebSocketChannelClient.*
import com.example.webrtcapp.net.AsyncHttpURLConnection
import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver
import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification
import de.tavendo.autobahn.WebSocketConnection
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.SessionDescription
import java.net.URI
import java.util.*

/**
 * WebSocket client implementation.
 *
 *
 *
 * All public methods should be called from a looper executor thread
 * passed in a constructor, otherwise exception will be thrown.
 * All events are dispatched on the same thread.
 */
class WebSocketChannelClient(
    private val handler: Handler, private val events: WebSocketChannelEvents
) {
    private var ws: WebSocketConnection? = null
    private var wsObserver: WebSocketObserver? = null
    private val wsServerUrl: String? = null
    private var postServerUrl: String? = null
    private var roomID: String? = null
    private var clientID: String? = null
    var state: WebSocketConnectionState
        private set
    private val closeEventLock = java.lang.Object()
    private var closeEvent = false

    // WebSocket send queue. Messages are added to the queue when WebSocket
    // client is not registered and are consumed in register() call.
    private val wsSendQueue: LinkedList<String>

    /**
     * Possible WebSocket connection states.
     */
    enum class WebSocketConnectionState {
        NEW, CONNECTED, REGISTERED, CLOSED, ERROR
    }

    /**
     * Callback interface for messages delivered on WebSocket.
     * All events are dispatched from a looper executor thread.
     */
    interface WebSocketChannelEvents {
        fun onWebSocketMessage(message: String?)
        fun onWebSocketClose()
        fun onWebSocketError(description: String?)

    }

    init {
        wsSendQueue = LinkedList()
        state = WebSocketConnectionState.NEW
    }

    fun connect(wsUrl: String, postUrl: String?) {
        postServerUrl = postUrl
        closeEvent = false
        ws = WebSocketConnection()
        wsObserver = WebSocketObserver()
        try {
            ws!!.connect(URI(wsUrl), wsObserver)
        } catch (e: Exception) {
        }
    }

    fun register(roomID: String?, clientID: String?) {
        this.roomID = roomID
        this.clientID = clientID
        if (state != WebSocketConnectionState.CONNECTED) {
            Log.w(
                TAG, "WebSocket register() in state $state"
            )
            return
        }
        val json = JSONObject()
        try {
            json.put("cmd", "register")
            json.put("roomid", roomID)
            json.put("clientid", clientID)
            ws!!.sendTextMessage(json.toString())
            state = WebSocketConnectionState.REGISTERED
            // Send any previously accumulated messages.
            for (sendMessage in wsSendQueue) {
                send(sendMessage)
            }
            wsSendQueue.clear()
        } catch (e: JSONException) {
            reportError("WebSocket register JSON error: " + e.message)
        }
    }

    fun send(message: String) {
        var message = message
        checkIfCalledOnValidThread()
        when (state) {
            WebSocketConnectionState.NEW, WebSocketConnectionState.CONNECTED -> {
                // Store outgoing messages and send them after websocket client
                // is registered.
                Log.d(TAG, "WS ACC: $message")
                wsSendQueue.add(message)
                return
            }
            WebSocketConnectionState.ERROR, WebSocketConnectionState.CLOSED -> {
                Log.e(
                    TAG, "WebSocket send() in error or closed state : $message"
                )
                return
            }
            WebSocketConnectionState.REGISTERED -> {
                val json = JSONObject()
                try {
                    json.put("cmd", "send")
                    json.put("msg", message)
                    message = json.toString()
                    Log.d(TAG, "C->WSS: $message")
                    ws!!.sendTextMessage(message)
                } catch (e: JSONException) {
                    reportError("WebSocket send JSON error: " + e.message)
                }
            }
        }
    }

    // This call can be used to send WebSocket messages before WebSocket
    // connection is opened.
    fun post(message: String) {
        checkIfCalledOnValidThread()
        sendWSSMessage("POST", message)
    }

    fun disconnect(waitForComplete: Boolean) {
        checkIfCalledOnValidThread()
        Log.d(
            TAG, "Disconnect WebSocket. State: $state"
        )
        if (state == WebSocketConnectionState.REGISTERED) {
            // Send "bye" to WebSocket server.
            send("{\"type\": \"bye\"}")
            state = WebSocketConnectionState.CONNECTED
            // Send http DELETE to http WebSocket server.
            sendWSSMessage("DELETE", "")
        }
        // Close WebSocket in CONNECTED or ERROR states only.
        if (state == WebSocketConnectionState.CONNECTED || state == WebSocketConnectionState.ERROR) {
            ws!!.disconnect()
            state = WebSocketConnectionState.CLOSED

            // Wait for websocket close event to prevent websocket library from
            // sending any pending messages to deleted looper thread.
            if (waitForComplete) {
                synchronized(closeEventLock) {
                    while (!closeEvent) {
                        try {
                            closeEventLock.wait(CLOSE_TIMEOUT.toLong())
                            break
                        } catch (e: InterruptedException) {
                            Log.e(
                                TAG, "Wait error: $e"
                            )
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Disconnecting WebSocket done.")
    }

    private fun reportError(errorMessage: String) {
        Log.e(TAG, errorMessage)
        handler.post {
            if (state != WebSocketConnectionState.ERROR) {
                state = WebSocketConnectionState.ERROR
                events.onWebSocketError(errorMessage)
            }
        }
    }

    // Asynchronously send POST/DELETE to WebSocket server.
    private fun sendWSSMessage(method: String, message: String) {
        val postUrl = "$postServerUrl/$roomID/$clientID"
        Log.d(TAG, "WS $method : $postUrl : $message")
        val httpConnection = AsyncHttpURLConnection(method,
            postUrl,
            message,
            object : AsyncHttpURLConnection.AsyncHttpEvents {
                override fun onHttpError(errorMessage: String) {
                    reportError("WS $method error: $errorMessage")
                }


                override fun onHttpComplete(response: String) {}
            })
        httpConnection.send()
    }

    // Helper method for debugging purposes. Ensures that WebSocket method is
    // called on a looper thread.
    private fun checkIfCalledOnValidThread() {
        check(!(Thread.currentThread() !== handler.looper.thread)) { "WebSocket method is not called on valid thread" }
    }

    private inner class WebSocketObserver : WebSocketConnectionObserver {
        override fun onOpen() {
            Log.d(TAG, "WebSocket connection opened to: $wsServerUrl")
            state = WebSocketConnectionState.CONNECTED
            register(roomID, clientID)
        }

        override fun onClose(code: WebSocketCloseNotification, reason: String) {
            Log.d(
                TAG,
                "WebSocket connection closed. Code: " + code + ". Reason: " + reason + ". State: " + state
            )
            synchronized(closeEventLock) {
                closeEvent = true
                closeEventLock.notify()
            }
            handler.post {
                if (state != WebSocketConnectionState.CLOSED) {
                    state = WebSocketConnectionState.CLOSED
                    events.onWebSocketClose()
                }
            }
        }

        override fun onTextMessage(payload: String) {
            Log.d(TAG, "WSS->C: $payload")
            handler.post {
                if (state == WebSocketConnectionState.CONNECTED || state == WebSocketConnectionState.REGISTERED) {
                    events.onWebSocketMessage(payload)
                }
            }
        }

        override fun onRawTextMessage(payload: ByteArray) {}
        override fun onBinaryMessage(payload: ByteArray) {}
    }

    companion object {
        private const val TAG = "WSChannelRTCClient"
        private const val CLOSE_TIMEOUT = 1000
    }
}