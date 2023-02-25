package com.snw.samllnewweather

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snw.samllnewweather.screen.HomeScreen

import com.snw.samllnewweather.screen.MainViewModel
import com.snw.samllnewweather.ui.theme.BgColor
import com.snw.samllnewweather.ui.theme.SmallNewWeahterTheme
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmallNewWeahterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgColor
                ) {
                    val viewModel: MainViewModel = viewModel()
                    val drawerState =
                        rememberScaffoldState(drawerState = rememberDrawerState(DrawerValue.Closed))
                    val coroutine = rememberCoroutineScope()
                    HomeScreen(viewModel, chooseLocationClick = {
                        coroutine.launch { drawerState.drawerState.open() }
                    }, drawerState = drawerState)
                }
            }
        }
    }
}
