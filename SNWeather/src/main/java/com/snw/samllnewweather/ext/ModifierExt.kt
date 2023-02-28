package com.snw.samllnewweather.ext

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight

import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

import com.snw.samllnewweather.ui.theme.plackholderColor
import com.snw.samllnewweather.ui.theme.plackholderHightColor

fun Modifier.smPlaceholder(visible: Boolean): Modifier = composed {
    this.placeholder(
        visible = visible, color = plackholderColor,
        highlight = PlaceholderHighlight.shimmer(highlightColor = plackholderHightColor),
        shape = RoundedCornerShape(4.dp)
    )
}