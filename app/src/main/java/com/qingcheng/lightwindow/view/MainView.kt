package com.qingcheng.lightwindow.view

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.service.VersionUpdateService
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.SharedPreferencesUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.util.runOnUI
import com.qingcheng.base.view.BaseFloatWindow
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.service.CldCoreService
import com.qingcheng.lightwindow.MainWindowService
import com.qingcheng.lightwindow.R
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * 主界面悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled")
class MainView(private val context: Context, viewManager: ViewManager) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.main, null)) {
    private val jsInterfaceName = "Android"

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(
                        context,
                        CacheName.MAIN_WIDTH.name
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, CacheName.MAIN_WIDTH.name)
            height =
                if (SharedPreferencesUtil.getInt(context, CacheName.MAIN_HEIGHT.name) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, CacheName.MAIN_HEIGHT.name)
        }
        applyView {
            findViewById<WebView>(R.id.wv_main).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("main版本", it)
                            if (it == "null") throwError()
                            SharedPreferencesUtil.put(
                                context,
                                CacheName.WEB_VERSION.name,
                                it
                            )
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        throwError()
                        super.onReceivedError(view, request, error)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(
                                this@MainView::class.qualifiedName,
                                "${message()} -- From line ${lineNumber()} of ${sourceId()}"
                            )
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                val zoom: () -> Unit = {
                    post {
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
                addJavascriptInterface(
                    JsInterface(
                        context,
                        mapOf(
                            "close" to { post { this@MainView.zoomOut() } },
                            "zoom" to zoom
                        )
                    ), jsInterfaceName
                )
                loadUrl("https://qingcheng.asia/guide/")
            }
        }
    }

    private fun throwError() {
        view.findViewById<WebView>(R.id.wv_main).destroy()
        this@MainView.view.visibility = View.GONE
        ToastUtil.showToast("加载失败")
        view.handler.postDelayed({
            context.stopService(
                Intent(
                    context,
                    MainWindowService::class.java
                )
            )
        }, 1500)
    }

    fun zoomIn() {
        view.scaleX = 0f
        view.scaleY = 0f
        addToWindow()
        view.animate().scaleX(1f).scaleY(1f).start()
    }

    fun zoomOut() {
        view.animate().scaleX(0f).scaleY(0f).withEndAction {
            view.visibility = View.GONE
            view.findViewById<WebView>(R.id.wv_main).destroy()
            removeFromWindow()
            context.stopService(Intent(context, MainWindowService::class.java))
        }
    }

    class JsInterface(
        private val context: Context,
        private val other: Map<String, () -> Unit>? = null
    ) {
        @JavascriptInterface
        fun close() {
            other?.get("close")?.invoke()
        }

        @JavascriptInterface
        fun showZoom() {
            other?.get("zoom")?.invoke()
        }

        @JavascriptInterface
        fun startCldService() {
            context.startForegroundService(Intent(context, CldCoreService::class.java))
            runOnUI { ToastUtil.showToast("日程表开始运行") }
        }

        @JavascriptInterface
        fun stopCldService() {
            context.stopService(Intent(context, CldCoreService::class.java))
        }

        @JavascriptInterface
        fun isCldRunning(): Boolean {
            val manager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
            val list = manager.getRunningServices(10)
            for (s in list) {
                if (s.service.className == CldCoreService::class.qualifiedName) return true
            }
            return false
        }

        @JavascriptInterface
        fun getClipboardText(): String {
            val manager = context.getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
            return "" + manager.primaryClip?.getItemAt(0)?.text
        }

        @JavascriptInterface
        fun startVersionService() {
            context.stopService(Intent(context, VersionUpdateService::class.java))
        }
    }
}