package com.snw.samllnewweather

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snw.samllnewweather.screen.HomeScreen
import com.snw.samllnewweather.ui.theme.BgColor
import com.snw.samllnewweather.ui.theme.SmallNewWeahterTheme
import com.snw.samllnewweather.viewmodel.MainViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPermissionMethod()
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


    private fun getPermissionMethod() {
        //授权列表
        val permissionList: MutableList<String> = ArrayList()

        //检查是否获取该权限 ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,

                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!permissionList.isEmpty()) { //权限列表不是空
            val permissions = permissionList.toTypedArray()
            //动态申请权限的请求
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        }
    }


}
