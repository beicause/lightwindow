package com.qingcheng.lightwindow.view

import android.app.ActivityManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.JavascriptInterface
import com.qingcheng.base.*
import com.qingcheng.base.service.VersionService
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.SharedPreferencesUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.util.VersionUtil
import com.qingcheng.base.view.BaseFloatWindow
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.service.CalendarNoticeService
import com.qingcheng.lightwindow.WebViewService
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.runBlocking

class MainJsInterface(
    private val context: Context,
    private val floatWindow: BaseFloatWindow<*>,
    private val viewManager: ViewManager,
) {

    @JavascriptInterface
    fun redirectToMain() {
        context.startService(Intent(context, WebViewService::class.java).apply {
            action = ACTION_START_MAIN
        })
    }

    @JavascriptInterface
    fun redirectToCalendar() {
        context.startService(Intent(context, WebViewService::class.java).apply {
            action = ACTION_START_CALENDAR
        })
    }

    @JavascriptInterface
    fun close() {
        floatWindow.view.post {
            floatWindow.zoomOut {
                context.stopService(Intent(context, WebViewService::class.java))
            }
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
    fun checkVersion(): String = runBlocking { VersionUtil.checkVersion(context) }

    @JavascriptInterface
    fun showVersionUpdate() = context.startService(Intent(context, VersionService::class.java))

    @JavascriptInterface
    fun getPolicy(): String = SharedPreferencesUtil.getString(context, POLICY)

    @JavascriptInterface
    fun setPolicy(value: String) {
        SharedPreferencesUtil.put(context, POLICY, value)
        if (value != "null") {
            if (!UMConfigure.isInit)
                UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        } else {
            close()
            context.stopService(Intent(context, CalendarNoticeService::class.java))
        }
    }
}