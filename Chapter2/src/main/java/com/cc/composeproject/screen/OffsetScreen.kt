package com.cc.composeproject.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


@Composable
fun OffsetScreen() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .offset { IntOffset(x = 30.dp.roundToPx(), y = 90.dp.roundToPx()) }
            .background(Color.Red)


    ) {


    }
}