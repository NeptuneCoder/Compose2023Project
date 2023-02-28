package com.snw.samllnewweather.ext

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.snw.samllnewweather.ui.screen.currentLocalPlaceholder
import com.snw.samllnewweather.ui.theme.Purple500
import com.snw.samllnewweather.ui.theme.plackholderColor

fun Modifier.smPlaceholder(visible: Boolean): Modifier = composed {
    this.placeholder(
        visible = visible, color = Purple500,
        highlight = PlaceholderHighlight.shimmer(highlightColor = plackholderColor),
        shape = RoundedCornerShape(4.dp)
    )
}