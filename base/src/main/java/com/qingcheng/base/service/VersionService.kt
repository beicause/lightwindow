package com.qingcheng.base.service

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.content.FileProvider
import com.qingcheng.base.IGNORE_VERSION
import com.qingcheng.base.util.*
import com.qingcheng.base.view.DialogView
import com.qingcheng.base.view.ViewManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class VersionService : Service() {

    private val apkUrl = "https://qingcheng.asia/app-release.apk"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        val viewManager = ViewManager(this)
        MainScope().launch {
            try {
                NetworkRequestUtil.getVersion().body?.string()
            } catch (e: Exception) {
                ToastUtil.showToast("检查网络并解除省流量限制")
                null
            }?.let { responseBody ->
                val json = JSONObject(responseBody)
                val appVersion = json.getString("app_version")
                val forceUpdate = json.getBoolean("force_update")
                val message = json.getString("version_info")
                viewManager.new<DialogView>(DialogView(this@VersionService)).apply {
                    maskClick = {
                        zoomOut()
                        stopSelf()
                    }
                    title = "发现新版本"
                    content = message
                    confirmText = if (!forceUpdate) "更新" else "强制性更新"
                    cancelText = if (!forceUpdate) "忽略" else "退出"
                    confirmClick = {
                        zoomOut()
                        downloadApp(this@VersionService, appVersion)
                    }
                    cancelClick = {
                        if (!forceUpdate) SharedPreferencesUtil.put(
                            this@VersionService,
                            IGNORE_VERSION,
                            appVersion
                        )
                        zoomOut()
                        stopSelf()
                    }
                    zoomIn()
                }
            }
        }
    }

    private fun downloadApp(service: Service, appVersion: String) {
        val apkName = appVersion.split("").let {
            it.subList(1, it.lastIndex).joinToString(".", "lightwindow-v", ".apk")
        }
        val apk = File(
            service.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path + "/apk/$apkName"
        )
        if (apk.exists()) {
            Log.i(apkName, "exist")
            service.startActivity(Intent().apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(
                    FileProvider.getUriForFile(
                        service,
                        "${service.packageName}.provider",
                        apk
                    ),
                    "application/vnd.android.package-archive"
                )
            })
            service.stopSelf()
        } else {
            val manager =
                service.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            ToastUtil.showToast("开始下载")
            val id = manager.enqueue(
                DownloadManager.Request(
                    Uri.parse(apkUrl)
                ).apply {
                    setMimeType("application/vnd.android.package-archive")
                    setDestinationInExternalFilesDir(
                        service,
                        Environment.DIRECTORY_DOWNLOADS, "/apk/$apkName"
                    )
                })
            val filter = IntentFilter().apply {
                addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            }
            val downloadReceiver = object : BroadcastReceiver() {
                override fun onReceive(mContext: Context?, intent: Intent?) {
                    Log.i("download receiver", intent?.action ?: "null")
                    when (intent?.action) {
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                            ToastUtil.showToast("下载完成")
                            service.startActivity(Intent().apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                setDataAndType(
                                    manager.getUriForDownloadedFile(id),
                                    "application/vnd.android.package-archive"
                                )
                            })
                            service.unregisterReceiver(this)
                            service.stopSelf()
                        }
                    }
                }
            }
            service.registerReceiver(downloadReceiver, filter)
        }

        val filter1 = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        val installReceiver = object : BroadcastReceiver() {
            override fun onReceive(mContext: Context?, intent: Intent?) {
                Log.i("install receiver", intent?.action ?: "null")
                when (intent?.action) {
                    Intent.ACTION_PACKAGE_REPLACED, Intent.ACTION_PACKAGE_ADDED -> {
                        FileUtil.deleteDir(
                            File(
                                service.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path + "/apk"
                            )
                        )
                        service.unregisterReceiver(this)
                    }
                }
            }

        }
        service.registerReceiver(installReceiver, filter1)
    }
}