package com.qingcheng.lightwindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.qingcheng.base.*
import com.qingcheng.base.R
import com.qingcheng.base.util.*
import com.qingcheng.base.view.FloatWebView
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.database.EventDataBase
import com.qingcheng.calendar.view.CalendarJsInterface
import com.qingcheng.lightwindow.view.MainJsInterface
import com.tencent.smtt.sdk.WebView
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WebViewService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var dataBase: EventDataBase? = null
    private var viewManager: ViewManager? = null
    private lateinit var floatView: FloatWebView
    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()
        viewManager = ViewManager(this)
        floatView =
            viewManager!!.new<FloatWebView>(FloatWebView(this, this::class.java)).apply {
                applyParams {
                    if (ScreenUtil.isLandscape(this@WebViewService)) {
                        val t = width
                        width = height
                        height = t
                    }
                }
            }
        dataBase =
            Room.databaseBuilder(applicationContext, EventDataBase::class.java, "events")
                .enableMultiInstanceInvalidation().build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == null) throw Exception("未指定action")
        MainScope().launch {
            VersionUtil.checkAndShowUpdate(this@WebViewService)
        }
        when (intent.action) {
            ACTION_START_CALENDAR -> {
                MobclickAgent.onEvent(this, "ACTION_START_CALENDAR")
                floatView.let {
                    it.view.findViewById<WebView>(R.id.webview).apply {
                        removeJavascriptInterface(JS_INTERFACE_NAME)
                        addJavascriptInterface(
                            CalendarJsInterface(context, it, dataBase!!),
                            JS_INTERFACE_NAME
                        )
                        it.loadUrl(CALENDAR_URL)
                    }
                    it.rotateIn()
                }
            }
            ACTION_START_MAIN -> {
                MobclickAgent.onEvent(this, "ACTION_START_MAIN")
                floatView.let {
                    it.view.findViewById<WebView>(R.id.webview).apply {
                        removeJavascriptInterface(JS_INTERFACE_NAME)
                        addJavascriptInterface(
                            MainJsInterface(context, it, viewManager!!),
                            JS_INTERFACE_NAME
                        )
                        it.loadUrl(MAIN_URL)
                    }
                    it.zoomIn()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        MobclickAgent.onEvent(this, "WEBVIEW_SERVICE_END")
        MobclickAgent.onKillProcess(this)
        dataBase?.close()
        viewManager?.destroyAll()
        viewManager = null
        Log.i(this::class.simpleName, "webview 服务 关闭")
    }
}