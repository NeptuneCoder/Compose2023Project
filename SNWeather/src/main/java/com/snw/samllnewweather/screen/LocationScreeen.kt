package com.snw.samllnewweather.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.snw.samllnewweather.ui.theme.BgColor

/**
 * 1. 默认显示当前地址和之前添加过的地址
 * 2. 输入地址，然后显示列表
 * 3. 选择添加：
 *      1. 添加到记录中
 *      2. 加载目标地址进行信息展示,关闭侧滑抽屉
 *
 */
@Composable
fun LocationScreeen() {
    var address by remember {
        mutableStateOf("")
    }
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(color = BgColor)
    ) {

        val (inputRef) = remember {
            createRefs()
        }

        OutlinedTextField(value = address, onValueChange = {
            address = it
        }, label = {
            Text("请输入地址")
        }, modifier = Modifier
            .constrainAs(inputRef) {
                top.linkTo(parent.top)
                absoluteLeft.linkTo(parent.absoluteLeft)
            }
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp))
    }
}