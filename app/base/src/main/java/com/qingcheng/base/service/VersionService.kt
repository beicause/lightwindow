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
import com.qingcheng.base.INDEX_URL
import com.qingcheng.base.calendarNoticeService
import com.qingcheng.base.uiWebViewServiceName
import com.qingcheng.base.util.FileUtil
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.util.VersionUtil
import com.qingcheng.base.view.DialogView
import com.qingcheng.base.view.ViewManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File

class VersionService : Service() {
    private val scope = MainScope()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        val viewManager = ViewManager(this)
        scope.launch {
            VersionUtil.checkVersion(this@VersionService).let { responseBody ->
                val json = JSONObject(responseBody)
                val appVersion = json.getString("app_version")
                val forceUpdate = json.getBoolean("force_update")
                val message = json.getString("version_info")

                viewManager.new<DialogView>(DialogView(this@VersionService)).apply {
                    maskClick = {
                        if (forceUpdate) {
                            stopService(Intent().apply {
                                setClassName(this@VersionService, uiWebViewServiceName)
                            })
                            stopService(Intent().apply {
                                setClassName(this@VersionService, calendarNoticeService)
                            })
                        }
                        zoomOut{stopSelf()}
                    }
                    title = if (forceUpdate) "发现新版本，该版本必须更新" else "发现新版本"
                    content = message
                    confirmText = if (!forceUpdate) "更新" else "必要更新"
                    cancelText = if (!forceUpdate) "忽略" else "退出"
                    confirmClick = {
                        zoomOut{stopSelf()}
                        downloadApp(this@VersionService, appVersion)
                    }
                    cancelClick = {
                        if (!forceUpdate) runBlocking {
                            PreferencesUtil
                                .putString(
                                    this@VersionService,
                                    IGNORE_VERSION, appVersion
                                )
                        } else {
                            stopService(Intent().apply {
                                setClassName(this@VersionService, uiWebViewServiceName)
                            })
                            stopService(Intent().apply {
                                setClassName(this@VersionService, calendarNoticeService)
                            })
                        }
                        zoomOut{stopSelf()}
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
        //如果已经下载
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
            stopService(Intent().apply {
                setClassName(service, uiWebViewServiceName)
            })
            service.stopSelf()
        } else {
            //开始下载

            val manager =
                service.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            ToastUtil.showToast("开始下载")
            val id = manager.enqueue(
                DownloadManager.Request(
                    Uri.parse("$INDEX_URL/$apkName")
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
            //监听下载完成
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
                            stopService(Intent().apply {
                                setClassName(service, uiWebViewServiceName)
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
        //监听安装
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