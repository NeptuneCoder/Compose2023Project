package com.snw.samllnewweather.screen

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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.snw.samllnewweather.R
import com.snw.samllnewweather.ui.theme.BgColor

@Composable
@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterialApi
fun HomeScreen(
    viewModel: MainViewModel,
    chooseLocationClick: (Int) -> Unit = {},
    drawerState: ScaffoldState,
) {
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


@OptIn(ExperimentalPagerApi::class)
@Composable
@ExperimentalMaterialApi
fun MainScreen(viewModel: MainViewModel, chooseLocationClick: (Int) -> Unit = {}) {
    val refreshing by viewModel.isRefreshing.collectAsState()
    val weatherInfo by viewModel.weatherData.collectAsState()

    val pullRefreshState = rememberPullRefreshState(refreshing, {/*TODO 刷新*/
        viewModel.refresh()
    })

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .background(color = BgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ListInfo(weatherInfo, chooseLocationClick)
        }

        PullRefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
@ExperimentalPagerApi
fun ListInfo(weatherInfo: WeatherInfo, chooseLocationClick: (Int) -> Unit = {}) {
    LazyColumn(Modifier.padding(start = 10.dp, end = 10.dp)) {
        item {
            TopMenu(weatherInfo, onClick = chooseLocationClick)
            Spacer(modifier = Modifier.height(30.dp))
            MainInfo(weatherInfo)
            Spacer(modifier = Modifier.height(14.dp))
            Future24Info(weatherInfo)
            Spacer(modifier = Modifier.height(14.dp))
            DetailInfo(weatherInfo)
            Spacer(modifier = Modifier.height(14.dp))
            ChineseCalendarInfo(weatherInfo)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(weatherInfo.futureDays.size) {
            ItemInfo(weatherInfo, it)
        }
    }

}

@Composable
fun TopMenu(weatherInfo: WeatherInfo, onClick: (Int) -> Unit = {}) {
    ConstraintLayout(
        Modifier
            .wrapContentHeight()
            .padding(top = 10.dp)
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
                append("${weatherInfo.address}")
            }
        }, modifier = Modifier.constrainAs(addressRef) {
            absoluteLeft.linkTo(iconRef.absoluteRight)
            bottom.linkTo(iconRef.bottom)

        }, onClick = onClick)
        Text(text = "${weatherInfo.publishTime}发布", modifier = Modifier
            .constrainAs(timeRef) {

                absoluteLeft.linkTo(addressRef.absoluteRight)
                baseline.linkTo(addressRef.baseline)
            }
            .padding(start = 10.dp), style = TextStyle())
    }
}

@Composable
fun MainInfo(weatherInfo: WeatherInfo) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (temperatureRef, riseTimeRef, infoRef, downTimeRef) = remember {
            createRefs()
        }
        Text(
            text = "${weatherInfo.temp}",
            modifier = Modifier.constrainAs(temperatureRef) {
                absoluteLeft.linkTo(parent.absoluteLeft)
                absoluteRight.linkTo(parent.absoluteRight)
            },
            style = MaterialTheme.typography.h2,
        )

        Text(
            text = "${weatherInfo.riseTime}",
            modifier = Modifier
                .constrainAs(riseTimeRef) {
                    baseline.linkTo(infoRef.baseline)
                    absoluteLeft.linkTo(parent.absoluteLeft)

                },
            style = MaterialTheme.typography.body1,
        )


        Text(
            text = " ${weatherInfo.downTime}",
            modifier = Modifier
                .constrainAs(downTimeRef) {
                    baseline.linkTo(infoRef.baseline)
                    absoluteRight.linkTo(parent.absoluteRight)
                },

            style = MaterialTheme.typography.body1,
        )

        Text(
            text = "${weatherInfo.info}",
            modifier = Modifier
                .constrainAs(infoRef) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    top.linkTo(temperatureRef.bottom)
                    absoluteRight.linkTo(parent.absoluteRight)
                }
                .padding(top = 10.dp),
            style = MaterialTheme.typography.body1,
        )

    }
}

@Composable
fun DetailInfo(weatherInfo: WeatherInfo) {
    Row {
        BodyTemperatureInfo(weatherInfo, Modifier.weight(1f))
        WindInfo(weatherInfo, Modifier.weight(1f))
        AirInfo(weatherInfo, Modifier.weight(1f))
    }
}

@Composable
fun BodyTemperatureInfo(weatherInfo: WeatherInfo, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "体感温度")
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 24.sp)) {
                append(weatherInfo.bodyTemp)
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
                append(weatherInfo.airLevel)
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
            append("${weatherInfo.chineseCalendarYear}\n")
        }
        withStyle(style = SpanStyle(fontSize = 24.sp)) {
            append(weatherInfo.chineseCalendarDay)
        }
        withStyle(style = SpanStyle(fontSize = 12.sp)) {
            append(" 兔年")
        }

    }, modifier = Modifier.padding())
}


@Composable
//@ExperimentalPagerApi
fun Future24Info(weatherInfo: WeatherInfo) {
//    val pagerState = rememberPagerState()
//    HorizontalPager(count = 24, Modifier.wrapContentWidth(), state = pagerState) {
//}
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (titleRef, contentRef) = remember {
            createRefs()
        }
        Text(text = "未来24小时", Modifier.constrainAs(titleRef) {
            absoluteLeft.linkTo(parent.absoluteLeft)
            top.linkTo(parent.top)
        }, fontSize = 16.sp)
        LazyRow(
            Modifier
                .constrainAs(contentRef) {
                    top.linkTo(titleRef.bottom)
                    absoluteLeft.linkTo(titleRef.absoluteLeft)
                }
                .padding(top = 6.dp)) {
            items(weatherInfo.futureHours.size) {
                Text(
                    text = "${weatherInfo.futureHours.get(it).time}\n${
                        weatherInfo.futureHours.get(
                            it
                        ).state
                    }",
                    Modifier
                        .padding(end = 10.dp)
                        .wrapContentSize(), fontSize = 12.sp

                )
            }
        }
    }
}


@Composable
fun ItemInfo(weatherInfo: WeatherInfo, index: Int) {
    Text(
        text = "空气状态：${weatherInfo.futureDays.get(index).state}",
        style = TextStyle(fontSize = 20.sp),
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}
