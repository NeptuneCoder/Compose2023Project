package com.cc.composeproject.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PaddingScreen() {
    Box(
        //从下往上修饰，链上最后的修饰最先作用到内容上
        modifier = Modifier
            .padding(30.dp)
            .border(2.dp, Color.Black, shape = RoundedCornerShape(2.dp))
            .padding(40.dp)
            .border(2.dp, Color.Blue, shape = RoundedCornerShape(2.dp))
            .padding(50.dp)
    ) {
        Spacer(
            modifier = Modifier
                .background(Color.Red)
                .size(width = 100.dp, height = 10.dp)
        )
    }
}