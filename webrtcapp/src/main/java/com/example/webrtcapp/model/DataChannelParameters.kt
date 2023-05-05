package com.example.webrtcapp.model

class DataChannelParameters(
    val ordered: Boolean, val maxRetransmitTimeMs: Int, val maxRetransmits: Int,
    val protocol: String, val negotiated: Boolean, val id: Int
)