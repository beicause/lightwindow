package com.qingcheng.lightwindow.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.room.Room
import com.qingcheng.base.*
import com.qingcheng.base.R
import com.qingcheng.base.util.*
import com.qingcheng.base.view.UIWebView
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.CalendarJsInterface
import com.qingcheng.calendar.database.EventDataBase
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UIWebViewService : Service() {

    private val scope = MainScope()
    private lateinit var dataBase: EventDataBase
    private lateinit var floatView: UIWebView
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()
        floatView =
            ViewManager.new<UIWebView>(UIWebView(this)).apply {
                applyParams {
                    if (ScreenUtil.isLandscape(this@UIWebViewService)) {
                        val t = width
                        width = height
                        height = t
                    }
                }
            }
        dataBase =
            Room.databaseBuilder(
                applicationContext,
                EventDataBase::class.java,
                EventDataBase.DATABASE_NAME
            )
                .enableMultiInstanceInvalidation().build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == null) throw Exception("未指定action")
        Log.i("uiWebview-action", "." + intent.action)
        scope.launch {
            VersionUtil.checkAndShowUpdate(this@UIWebViewService)
        }
        when (intent.action) {
            ACTION_START_CALENDAR -> {
                MobclickAgent.onEvent(this, "ACTION_START_CALENDAR")
                floatView.let {
                    it.view.findViewById<WebView>(R.id.webview).apply {
                        removeJavascriptInterface(JS_INTERFACE_NAME)
                        addJavascriptInterface(
                            CalendarJsInterface(context, it, dataBase),
                            JS_INTERFACE_NAME
                        )
                        it.loadUrl(CALENDAR_URL)
                    }
                    it.rotateIn()
                }
            }
            ACTION_START_MAIN -> {
                MobclickAgent.onEvent(this, "ACTION_START_MAIN")
                val url = MAIN_URL
                floatView.let {
                    it.view.findViewById<WebView>(R.id.webview).apply {
                        removeJavascriptInterface(JS_INTERFACE_NAME)
                        addJavascriptInterface(
                            MainJsInterface(context),
                            JS_INTERFACE_NAME
                        )
                        it.loadUrl(url)
                    }
                    it.zoomIn()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
        MobclickAgent.onEvent(this, "WEBVIEW_SERVICE_END")
        dataBase.close()
        ViewManager.destroy(UIWebView::class)
        Log.i(this::class.simpleName, "webview 服务 关闭")
    }
}
