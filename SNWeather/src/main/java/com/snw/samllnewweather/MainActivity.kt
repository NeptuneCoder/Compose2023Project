package com.snw.samllnewweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.snw.samllnewweather.screen.HomeScreen
import com.snw.samllnewweather.ui.theme.BgColor
import com.snw.samllnewweather.ui.theme.SmallNewWeahterTheme
import com.snw.samllnewweather.viewmodel.MainViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalPermissionsApi
@AndroidEntryPoint
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
                    FeatureThatRequiresCameraPermission(viewModel, drawerState)
                    HomeScreen(viewModel, chooseLocationClick = {
                        coroutine.launch { drawerState.drawerState.open() }
                    }, drawerState = drawerState)
                }
            }
        }
    }

}


@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalPermissionsApi
@Composable
fun FeatureThatRequiresCameraPermission(viewModel: MainViewModel, scaffoldState: ScaffoldState) {

    val cameraPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    SideEffect {
        when {
            cameraPermissionState.hasPermission -> {
                viewModel.startLocation()
            }
            else -> {
                if (cameraPermissionState.shouldShowRationale) {
                    //需向用户说明为什么要该权限
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        }
    }


}