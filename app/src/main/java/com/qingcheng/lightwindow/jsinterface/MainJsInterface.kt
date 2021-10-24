package com.qingcheng.lightwindow.jsinterface

import android.app.ActivityManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import androidx.core.os.postDelayed
import com.qingcheng.base.*
import com.qingcheng.base.service.VersionService
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.BaseFloatWindow
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.service.CalendarNoticeService
import com.qingcheng.lightwindow.UIWebViewService
import com.qingcheng.lightwindow.view.ZoomView
import com.umeng.commonsdk.UMConfigure

class MainJsInterface(
    private val context: Context,
    private val floatWindow: BaseFloatWindow<*>,
    private val viewManager: ViewManager,
) {

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
        floatWindow.view.post {
            floatWindow.applyParams {
                if (ScreenUtil.isLandscape(context)) {
                    val t = width
                    width = height
                    height = t
                }
                PreferencesUtil.putString(context, MAIN_WIDTH, width.toString())
                PreferencesUtil.putString(context, MAIN_HEIGHT, height.toString())

            }
            floatWindow.zoomOut {
                context.stopService(Intent(context, UIWebViewService::class.java))
            }
        }
    }

    @JavascriptInterface
    fun exception(s: String) {
        runOnUI { ToastUtil.showToast(s, isLong = true) }
        floatWindow.zoomOut()
        Handler(Looper.getMainLooper()).postDelayed(3000) {
            context.stopService(Intent(context, UIWebViewService::class.java))
        }
    }

    @JavascriptInterface
    fun showZoom() {
        floatWindow.view.post {
            viewManager.new<ZoomView>(ZoomView(context, viewManager)).apply {
                addToWindow()
                view.visibility = View.VISIBLE
                setPosition(0, 0)
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
    fun isNoticeRunning(): Boolean {
        val manager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        val list = manager.getRunningServices(10)
        for (s in list) {
            if (s.service.className == CalendarNoticeService::class.qualifiedName) return true
        }
        return false
    }

    @JavascriptInterface
    fun getClipboardText(): String {
        val manager = context.getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
        return "" + manager.primaryClip?.getItemAt(0)?.text
    }

    @JavascriptInterface
    fun getAppVersion(): String =
        context.packageManager.getPackageInfo(context.packageName, 0).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode else it.versionCode
        }.toString()

    @JavascriptInterface
    fun showVersionUpdate() = context.startService(Intent(context, VersionService::class.java))

    @JavascriptInterface
    fun getPolicy(): String =
        PreferencesUtil.getString(context, POLICY) ?: "null"

    @JavascriptInterface
    fun setPolicy(value: String) {
        PreferencesUtil.putString(context, POLICY, value)

        if (value != "null") {
            if (!UMConfigure.isInit)
                UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        } else {
            close()
            context.stopService(Intent(context, CalendarNoticeService::class.java))
        }
    }
}