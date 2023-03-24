package com.example.bsdiff

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {
    init {
        System.loadLibrary("native-lib")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.version).text = BuildConfig.VERSION_NAME


    }

    val coroutines = CoroutineScope(Job())

    fun update(view: View?) {
        coroutines.launch {


            val old = application.applicationInfo.sourceDir
            patch(
                old,
                "${Environment.getExternalStorageDirectory().path + File.separator}patch",
                "${Environment.getExternalStorageDirectory().path + File.separator}new.apk"
            )
            val file = File(
                "${
                    Environment.getExternalStorageDirectory().path + File.separator
                }new.apk"
            )

            val intent = Intent(Intent.ACTION_VIEW)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(
                    Uri.fromFile(file),
                    "application/vnd.android.package-archive"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            } else {
                // 声明需要的临时权限
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                // 第二个参数，即第一步中配置的authorities
                val packageName = application.packageName
                val contentUri: Uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "$packageName.fileProvider", file
                )
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            }
            startActivity(intent)
        }
        //1、合成 apk
        //先从服务器下载到差分包

        //2、安装
    }

    /**
     * 根据差分包生成新的按照包
     * oldApk：老的安装包
     * patch：服务器下载下来的差分包
     * output：新的安装包
     */
    external fun patch(oldApk: String, patch: String, output: String)
}