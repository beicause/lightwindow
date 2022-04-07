package com.qingcheng.lightwindow.ui

import android.app.ActivityManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import com.qingcheng.base.*
import com.qingcheng.base.service.VersionService
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.UIWebView
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.service.CalendarNoticeService
import com.qingcheng.lightwindow.genshin.GenshinWebViewService

class MainJsInterface(
    private val context: Context,
) : BaseJsInterface(context, ViewManager.get(UIWebView::class)!!.view.findViewById(R.id.webview)) {

    private val uiWebView = ViewManager.get(UIWebView::class) as UIWebView

    @JavascriptInterface
    fun redirectToMain() {
        context.startService(Intent(context, UIWebViewService::class.java).apply {
            action = ACTION_START_MAIN
        })
    }

    @JavascriptInterface
    fun redirectToCalendar() {
        context.startService(Intent(context, UIWebViewService::class.java).apply {
            action = ACTION_START_CALENDAR
        })
    }

    @JavascriptInterface
    fun close() {
        uiWebView.view.post {
            uiWebView.applyParams {
                if (ScreenUtil.isLandscape(context)) {
                    val t = width
                    width = height
                    height = t
                }
                PreferencesUtil.putString(context, MAIN_WIDTH, width.toString())
                PreferencesUtil.putString(context, MAIN_HEIGHT, height.toString())

            }
            uiWebView.zoomOut {
                context.stopService(Intent(context, UIWebViewService::class.java))
            }
        }
    }

    @JavascriptInterface
    fun showZoom() {
        uiWebView.view.post {
            ViewManager.new<ZoomView>(ZoomView(context)).apply {
                addToWindow()
                view.post {
                    moveTo(
                        toX = (ScreenUtil.getWidth(context) - view.width) / 2,
                        toY = (ScreenUtil.getHeight(context) - view.height) / 2
                    )
                }
            }
        }
    }

    @JavascriptInterface
    fun startNoticeService() {
        context.startForegroundService(Intent(context, CalendarNoticeService::class.java))
        runOnUI { ToastUtil.showToast("日程表开始运行") }
    }

    @JavascriptInterface
    fun stopNoticeService() {
        context.stopService(Intent(context, CalendarNoticeService::class.java))
    }

    @JavascriptInterface
    fun isNoticeRunning(): Boolean = isServiceRunning(CalendarNoticeService::class.qualifiedName)

    @JavascriptInterface
    fun getClipboardText(): String {
        val manager = context.getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
        return manager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    @JavascriptInterface
    fun getAppVersion(): String {
        val version = context.packageManager.getPackageInfo(context.packageName, 0).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode else it.versionCode
        }.toString()
        Log.i("jsinterface", "web get appVersion: $version")
        return version
    }

    @JavascriptInterface
    fun showVersionUpdate() = context.startService(Intent(context, VersionService::class.java))

    @JavascriptInterface
    fun getPolicy(): String =
        PreferencesUtil.getString(context, POLICY) ?: "null"

    @JavascriptInterface
    fun setPolicy(value: String) {
        PreferencesUtil.putString(context, POLICY, value)
        if (value != "null") {
//            if (!UMConfigure.isInit)
//                UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        } else {
            close()
            context.stopService(Intent(context, CalendarNoticeService::class.java))
        }
    }

    @JavascriptInterface
    fun showGenshin() {
        context.startService(Intent(context, GenshinWebViewService::class.java))
    }

    @JavascriptInterface
    fun closeGenshin() {
        context.stopService(Intent(context, GenshinWebViewService::class.java))
    }

    @JavascriptInterface
    fun isGenshinRunning() = isServiceRunning(GenshinWebViewService::class.qualifiedName)

    @JavascriptInterface
    fun showSnow() {
        context.startService(Intent(context, UIService::class.java).apply {
            action = UIService.ACTION_SHOW_SNOW
        })
    }

    @JavascriptInterface
    fun isSnowRunning() = PreferencesUtil.getString(context,UIService.IS_SNOW_RUNNING)=="1"

    @JavascriptInterface
    fun closeSnow() {
        context.startService(Intent(context, UIService::class.java).apply {
            action = UIService.ACTION_CLOSE_SNOW
        })
    }

    private fun isServiceRunning(className: String?): Boolean {
        val manager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        val list = manager.getRunningServices(10)
        for (s in list) {
            if (s.service.className == className) return true
        }
        return false
    }
}
