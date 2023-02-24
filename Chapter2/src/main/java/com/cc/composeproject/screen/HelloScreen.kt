package com.cc.composeproject.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


@Composable
fun HelloScreen() {
    var name by rememberSaveable {
        mutableStateOf("")
    }
    HelloContent(value = name, onValueChange = {
        name = it
    })
}


@Composable
fun HelloContent(value: String, onValueChange: (String) -> Unit) {


    Column {
        Text(text = "value:$value")
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = "请输入内容") }
        )
    }

}


