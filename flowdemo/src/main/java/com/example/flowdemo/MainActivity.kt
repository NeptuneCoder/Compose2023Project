package com.example.flowdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowdemo.ui.theme.Compose2023ProjectTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose2023ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        ColdFlowMultiCollectButton {
                            coldFlowMultiCollect()
                        }
                        SharedFlowMultiCollectButton {
                            SharedFlowMultiCollect()
                        }
                    }
                }
            }
        }
    }

    /**在冷流时，多个收集器都会收到数据。先一个收集器接收安后，另一个才会继续📱。
    I/collect: collect 1 value = 1
    I/collect: collect 1 value = 2
    I/collect: collect 1 value = 3
    I/collect: collect 1 value = 4
    I/collect: collect 1 value = 5
    I/collect: collect 2 value = 1
    I/collect: collect 2 value = 2
    I/collect: collect 2 value = 3
    I/collect: collect 2 value = 4
    I/collect: collect 2 value = 5
     */
    private fun coldFlowMultiCollect() {
        val flow = flow<Int> {
            (1..5).forEach {
                emit(it)
            }
        }
        GlobalScope.launch {
            flow.collect {
                Log.i("collect", "collect 1 value = $it")
            }
        }
        GlobalScope.launch {
            flow.collect {
                Log.i("collect", "collect 2 value = $it")
            }
        }
    }

    /**
     * 1. 只要在发送之前注册了collect就一定能收到，不管是否延迟
     * 2. 在发送完成之前注册的collect就能收到剩余的数据。
     */
    private fun SharedFlowMultiCollect() {
        val flow = MutableSharedFlow<Int>()
        GlobalScope.launch {
            flow.collect {
                Log.i("collect", "sharedFlow collect 1 value = $it")
            }
        }
        GlobalScope.launch {
            flow.collect {
                Log.i("collect", "sharedFlow collect 2 value = $it")
            }
        }
        GlobalScope.launch {
            flow.collect {
                Log.i("collect", "sharedFlow collect 3 value = $it")
                delay(300)
            }
        }
        GlobalScope.launch {
            (1..5).forEach {
                flow.emit(it)
            }
        }

        GlobalScope.launch {
            delay(2000)
            flow.collect {
                Log.i("collect", "sharedFlow collect 4 value = $it")

            }
        }
    }
}

@Composable
fun ColdFlowMultiCollectButton(click: () -> Unit) {
    Button(
        onClick = click,
        Modifier
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        Text(text = "测试冷流多个收集器测试")
    }
}

@Composable
fun SharedFlowMultiCollectButton(click: () -> Unit) {
    Button(
        onClick = click,
        Modifier
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        Text(text = "SharedFlow流多个收集器测试")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Compose2023ProjectTheme {
        Greeting("Android")
    }
}