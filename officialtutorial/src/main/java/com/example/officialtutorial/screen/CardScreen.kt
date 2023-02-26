package com.example.officialtutorial.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column

import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun CardScreen() {
    Card {
        var isExpand by remember {
            mutableStateOf(false)
        }
        Column(Modifier.clickable { isExpand = !isExpand }) {

        }
    }
}