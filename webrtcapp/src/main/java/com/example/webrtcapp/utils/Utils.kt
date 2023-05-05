package com.example.webrtcapp.utils

import android.content.Context
import com.example.webrtcapp.model.RoomConnectionParameters
import com.example.webrtcapp.model.SignalingParameters
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.VideoCapturer
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {
    //接口
    fun getMessageUrl(
        connectionParameters: RoomConnectionParameters, signalingParameters: SignalingParameters
    ): String {
        return (connectionParameters.roomUrl + "/message/" + connectionParameters.roomId
                + "/" + signalingParameters.clientId)
    }

    fun getLeaveUrl(
        connectionParameters: RoomConnectionParameters, signalingParameters: SignalingParameters
    ): String {
        return (connectionParameters.roomUrl + "/message/" + connectionParameters.roomId + "/"
                + signalingParameters.clientId)
    }

    ///VideoCapturer  Camaera
    fun createVideoCaptuer(context: Context?): VideoCapturer? {
//
        val videoCapturer: VideoCapturer?
        videoCapturer = if (Camera2Enumerator.isSupported(context)) {
            //Camera2
            createCameraCapturer(Camera2Enumerator(context))
        } else {
            //Camera
            createCameraCapturer(Camera1Enumerator(true))
        }
        return videoCapturer
    }

    fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    fun preferCodec(sdpDescription: String, codec: String, isAudio: Boolean): String? {
        val lines = sdpDescription.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        var mLineIndex = -1
        var codecRtpMap: String? = null
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        val regex = "^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$"
        val codecPattern: Pattern = Pattern.compile(regex)
        var mediaDescription = "m=video "
        if (isAudio) {
            mediaDescription = "m=audio "
        }
        var i = 0
        while (i < lines.size && (mLineIndex == -1 || codecRtpMap == null)) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i
                i++
                continue
            }
            val codecMatcher: Matcher = codecPattern.matcher(lines[i])
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1)
            }
            i++
        }
        if (mLineIndex == -1) {
            return sdpDescription
        }
        if (codecRtpMap == null) {
            return sdpDescription
        }
        val origMLineParts = lines[mLineIndex].split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (origMLineParts.size > 3) {
            val newMLine = StringBuilder()
            var origPartIndex = 0
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ")
            newMLine.append(origMLineParts[origPartIndex++]).append(" ")
            newMLine.append(origMLineParts[origPartIndex++]).append(" ")
            newMLine.append(codecRtpMap)
            while (origPartIndex < origMLineParts.size) {
                if (origMLineParts[origPartIndex] != codecRtpMap) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex])
                }
                origPartIndex++
            }
            lines[mLineIndex] = newMLine.toString()
        } else {
        }
        val newSdpDescription = StringBuilder()
        for (line in lines) {
            newSdpDescription.append(line).append("\r\n")
        }
        return newSdpDescription.toString()
    }
}