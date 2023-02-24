package com.snw.samllnewweather.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.snw.samllnewweather.R
import com.snw.samllnewweather.ui.theme.Purple500

@Composable
fun MainScree() {
    Column(
        modifier = Modifier

            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Blue)
        ) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Cyan)
        ) {
            Text(text = "内容")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Gray)
        ) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .background(Color.Magenta)
        ) {

        }

    }
}