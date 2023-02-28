#### 项目说明

#### 功能介绍

1. 显示当前位置的天气信息
2. 可以搜索不同城市的天气信息
3. 模式选择：关怀模式,可以语音播报
4. 主题选择：白天，黑夜(跟随系统)，其他
5. 天气状态动画展示
6. 未来24小时天气状态显示（自定义图片展示）
7. 城市列表本地缓存

#### 项目架构及三方SDK说明

##### 1. 基本说明

1. 项目采用mvi架构；原生组件有：Compose + ViewModel + Hilt + retrofit + Room + Flow + accompanist*
2. 第三方sdk：百度地图sdk.和风api

##### 2. 控件及布局说明

1. 采用ConstraintLayout,Row,Column,LazyColumn等布局
2. Box，Text,ClickableText,OutlinedTextField,PullRefreshIndicator等组件

##### 3. 其他技术

1. 使用CompositionLocalProvider实现数据在函数间穿透，避免大量函数参数造成的问题
2. 状态上提，方便全局进行管理
3. 在ViewModel中对BaiduSdk进行初始化

#### 使用资源说明

ICON使用资源：https://www.iconfont.cn/user/detail?spm=a313x.7781069.0.d214f71f6&uid=757922&nid=u9q8JjfcFdqq

##### 和风天气

https://console.qweather.com/#/apps

#### 需要ApplicationContext上下文

```kotlin
class TestApplication @Inject constructor(private val context: Context) {

    fun appIsNull() {
        Log.i("appIsNull", "appIsNull == ${context == null}")
    }
}

@Module
@InstallIn(SingletonComponent::class)
class TestApplicationModule {
    @Singleton
    @Provides
    fun providerTestMethod(@ApplicationContext app: Context): TestApplication {
        Log.i("app", "app is null = ${app == null}")
        return TestApplication(app)
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val snapi: SNNetService,
    @ApplicationContext private val context: Context,
    private val app: TestApplication

) : ViewModel() {

}

```


