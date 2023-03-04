package com.snw.samllnewweather.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi

import com.snw.samllnewweather.R
import com.snw.samllnewweather.ext.formatTemp
import com.snw.samllnewweather.ext.formatTime
import com.snw.samllnewweather.ext.smPlaceholder
import com.snw.samllnewweather.model.DayInfo
import com.snw.samllnewweather.screen.WeatherInfo
import com.snw.samllnewweather.ui.theme.BgColor
import com.snw.samllnewweather.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterialApi
fun HomeScreen(
    viewModel: MainViewModel,
    chooseLocationClick: (Int) -> Unit = {},
    drawerState: ScaffoldState,
) {
    val coroutine = rememberCoroutineScope()
    val errMsg by viewModel.errMsg.collectAsState()

    if (errMsg.isNotBlank()) {
        coroutine.launch {
            drawerState.snackbarHostState.showSnackbar(errMsg)
        }
    }
    Scaffold(
        Modifier
            .fillMaxSize()
            .background(color = BgColor),
        scaffoldState = drawerState,
        drawerContent = {
            LocationScreeen()
        }) {
        MainScreen(viewModel, chooseLocationClick)
    }
}


val currentLocalData = compositionLocalOf { WeatherInfo() }
val currentLocalPlaceholder = compositionLocalOf { true }


@OptIn(ExperimentalPagerApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
@ExperimentalMaterialApi
fun MainScreen(viewModel: MainViewModel, chooseLocationClick: (Int) -> Unit = {}) {
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val weatherInfo by viewModel.weatherData.collectAsState()
    val showPlaceholder by viewModel.showPlaceholder.collectAsState()


    val pullRefreshState = rememberPullRefreshState(refreshing, {/*TODO 刷新*/
        viewModel.refresh()
    })


    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .background(color = BgColor)
    ) {
        CompositionLocalProvider(
            currentLocalData provides weatherInfo,
            currentLocalPlaceholder provides showPlaceholder
        ) {
            val dayData = currentLocalData.current.futureDays
            LazyColumn(
                Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .fillMaxSize()
            ) {
                item {

                    TopMenu(onClick = chooseLocationClick)
                    Spacer(modifier = Modifier.height(30.dp))
                    MainInfo()
                    Spacer(modifier = Modifier.height(14.dp))
                    Future24Info()
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailInfo(weatherInfo)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "未来7天天气情况",
                        fontSize = 18.sp,
                        modifier = Modifier.smPlaceholder(currentLocalPlaceholder.current)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                }
                items(dayData.size) {
                    ItemDayInfo(dayInfo = dayData[it])
                }
            }

        }

        PullRefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}


@Composable
fun TopMenu(onClick: (Int) -> Unit = {}) {
    ConstraintLayout(
        Modifier
            .wrapContentHeight()
            .padding(top = 10.dp)
            .smPlaceholder(
                visible = currentLocalPlaceholder.current
            )
    ) {
        val (iconRef, addressRef, timeRef) = remember {
            createRefs()
        }
        Icon(
            painter = painterResource(id = R.mipmap.ic_location),
            contentDescription = null,
            modifier = Modifier.constrainAs(iconRef) {
                top.linkTo(parent.top)
                absoluteLeft.linkTo(parent.absoluteLeft)
            })
        ClickableText(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 18.sp)) {
                append("${currentLocalData.current.address}")
            }
        }, modifier = Modifier.constrainAs(addressRef) {
            absoluteLeft.linkTo(iconRef.absoluteRight)
            bottom.linkTo(iconRef.bottom)

        }, onClick = onClick)
        Text(text = "${currentLocalData.current.publishTime}发布", modifier = Modifier
            .constrainAs(timeRef) {

                absoluteLeft.linkTo(addressRef.absoluteRight)
                baseline.linkTo(addressRef.baseline)
            }
            .padding(start = 10.dp), style = TextStyle())
    }
}

@Composable
fun MainInfo() {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (temperatureRef, riseTimeRef, infoRef, downTimeRef, iconRef) = remember {
            createRefs()
        }


        Image(
            painter = painterResource(id = currentLocalData.current.icon),
            contentDescription = null, modifier = Modifier
                .constrainAs(iconRef) {
                    absoluteRight.linkTo(temperatureRef.absoluteLeft)
                    top.linkTo(temperatureRef.top)
                    bottom.linkTo(temperatureRef.bottom)
                }
                .smPlaceholder(currentLocalPlaceholder.current)
        )
        Text(
            text = "${currentLocalData.current.temp}",
            modifier = Modifier
                .constrainAs(temperatureRef) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }
                .smPlaceholder(currentLocalPlaceholder.current),
            style = MaterialTheme.typography.h2,
        )

        Text(
            text = "${currentLocalData.current.riseTime}",
            modifier = Modifier
                .constrainAs(riseTimeRef) {
                    baseline.linkTo(infoRef.baseline)
                    absoluteLeft.linkTo(parent.absoluteLeft)

                }
                .smPlaceholder(currentLocalPlaceholder.current),
            style = MaterialTheme.typography.body1,
        )


        Text(
            text = " ${currentLocalData.current.downTime}",
            modifier = Modifier
                .constrainAs(downTimeRef) {
                    baseline.linkTo(infoRef.baseline)
                    absoluteRight.linkTo(parent.absoluteRight)
                }
                .smPlaceholder(currentLocalPlaceholder.current),

            style = MaterialTheme.typography.body1,
        )

        Text(
            text = "${currentLocalData.current.text} ${currentLocalData.current.tempMin} ~ ${currentLocalData.current.tempMax}",
            modifier = Modifier
                .constrainAs(infoRef) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    top.linkTo(temperatureRef.bottom)
                    absoluteRight.linkTo(parent.absoluteRight)
                }
                .padding(top = 10.dp)
                .smPlaceholder(currentLocalPlaceholder.current),
            style = MaterialTheme.typography.body1,
        )

    }
}

@Composable
fun DetailInfo(weatherInfo: WeatherInfo) {
    Row {
        BodyTemperatureInfo(
            weatherInfo,
            Modifier
                .weight(1f)
                .smPlaceholder(currentLocalPlaceholder.current)
                .padding(end = 10.dp)
        )
        WindInfo(
            weatherInfo,
            Modifier
                .weight(1f)
                .smPlaceholder(currentLocalPlaceholder.current)
                .padding(end = 10.dp, start = 10.dp)
        )
        AirInfo(
            weatherInfo,
            Modifier
                .weight(1f)
                .smPlaceholder(currentLocalPlaceholder.current)
                .padding(start = 10.dp)
        )
    }
}

@Composable
fun BodyTemperatureInfo(weatherInfo: WeatherInfo, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "体感温度")
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 24.sp)) {
                append(weatherInfo.feelTemp)
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("°C")
            }
        })
    }
}

@Composable
fun WindInfo(weatherInfo: WeatherInfo, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = weatherInfo.windDirect)
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 24.sp)) {
                append(weatherInfo.windLevel)
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("级")
            }
        })
    }
}

@Composable
fun AirInfo(weatherInfo: WeatherInfo, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(text = "空气${weatherInfo.airState}")
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 24.sp)) {
                append(weatherInfo.airAqi)
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("AQI")
            }
        })
    }
}

@Composable
fun ChineseCalendarInfo(weatherInfo: WeatherInfo) {
    Text(text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append("${weatherInfo}\n")
        }
        withStyle(style = SpanStyle(fontSize = 24.sp)) {
            append("")
        }
        withStyle(style = SpanStyle(fontSize = 12.sp)) {
            append(" 兔年")
        }

    }, modifier = Modifier.padding())
}


@Composable
//@ExperimentalPagerApi
fun Future24Info() {
    val data = currentLocalData.current.futureHours
    val size = data.size
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .smPlaceholder(currentLocalPlaceholder.current)
    ) {
        val (titleRef, contentRef) = remember {
            createRefs()
        }
        Text(text = "未来24小时", Modifier.constrainAs(titleRef) {
            absoluteLeft.linkTo(parent.absoluteLeft)
            top.linkTo(parent.top)
        }, fontSize = 18.sp)
        LazyRow(
            Modifier
                .constrainAs(contentRef) {
                    top.linkTo(titleRef.bottom)
                    absoluteLeft.linkTo(titleRef.absoluteLeft)
                }
                .padding(top = 6.dp)) {
            items(size) {
                val data = data.get(it)
                Column(
                    Modifier
                        .padding(end = 10.dp), horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = data.fxTime.formatTime(SimpleDateFormat("HH")) + " 时",
                        Modifier
                            .padding(end = 10.dp)
                            .wrapContentSize(), fontSize = 12.sp
                    )

                    Row {
                        Image(
                            painter = painterResource(id = data.iconId),
                            contentDescription = null,
                        )
                        Text(text = " " + data.temp.formatTemp())
                    }

                    Text(
                        text = data.windDir + " : " + data.windScale.replace(
                            "-",
                            "~"
                        ),
                        Modifier
                            .padding(end = 10.dp)
                            .wrapContentSize(), fontSize = 12.sp

                    )
                }

            }
        }
    }
}


@Composable
fun ItemDayInfo(dayInfo: DayInfo) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp)
            .smPlaceholder(
                visible = currentLocalPlaceholder.current
            )
    ) {

        val (timeRef, stateRef, winRef) = remember {
            createRefs()
        }
        Text(
            text = dayInfo.fxDate,
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .constrainAs(timeRef) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 12.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append("白天:")
                }
                withStyle(style = SpanStyle(fontSize = 16.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append(dayInfo.textDay)
                }
                withStyle(style = SpanStyle(fontSize = 12.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append("夜间:")
                }
                withStyle(style = SpanStyle(fontSize = 16.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append(dayInfo.textNight)
                }
                withStyle(style = SpanStyle(fontSize = 16.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append("\n")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
//                    "白天：" + dayInfo.textDay + " " + "夜间：" + dayInfo.textNight,
                    append("温度：" + dayInfo.tempMin.formatTemp() + "~" + dayInfo.tempMax.formatTemp())
                }
            },
            modifier = Modifier
                .constrainAs(stateRef) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    top.linkTo(timeRef.bottom)
                }
        )
        Text(
            text = "${dayInfo.windDirDay}:${dayInfo.windScaleDay}" + "\n" + dayInfo.windSpeedDay + "km/h",
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .constrainAs(winRef) {
                    absoluteRight.linkTo(parent.absoluteRight)
                }
        )
    }
}


