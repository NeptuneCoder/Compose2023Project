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

/**

总结：
SharedFlow和StateFlow的侧重点

StateFlow就是一个replaySize=1的sharedFlow,同时它必须有一个初始值，此外，每次更新数据都会和旧数据做一次比较，
只有不同时候才会更新数值。StateFlow重点在状态，ui永远有状态，所以StateFlow必须有初始值，同时对ui而言，过期的状态毫无意义，
所以stateFLow永远更新最新的数据（和liveData相似），所以必须有粘滞度=1的粘滞事件，让ui状态保持到最新。另外在一个时间内发送多个事件，
不会管中间事件有没有消费完成都会执行最新的一条.(中间值会丢失)

参数说明：
replay:告诉收集器最少能收集到几个数据
extraBufferCapacity：缓存容量
onBufferOverflow：由背压就有处理策略，sharedflow默认为BufferOverflow.SUSPEND，也即是如果当事件数量超过缓存，
发送就会被挂起，上面提到了一句，DROP_OLDEST销毁最旧的值，DROP_LATEST销毁最新的值

SharedFlow侧重在事件，当某个事件触发，发送到队列之中，按照挂起或者非挂起、缓存策略等将事件发送到接受方，在具体使用时，
SharedFlow更适合通知ui界面的一些事件，比如toast等，也适合作为viewModel和repository之间的桥梁用作数据的传输。


 */
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