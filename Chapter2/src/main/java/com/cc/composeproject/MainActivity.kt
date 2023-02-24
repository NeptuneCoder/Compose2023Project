package com.cc.composeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*


import androidx.compose.ui.Modifier
import com.cc.composeproject.screen.OffsetScreen
import com.cc.composeproject.screen.PaddingScreen
import com.cc.composeproject.ui.theme.ComposeCollectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeCollectionTheme {
                OffsetScreen()
            }
        }
    }
}
