package com.qingcheng.lightwindow.view

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageView
import com.qingcheng.lightwindow.R
import com.qingcheng.lightwindow.cache.CacheName
import com.qingcheng.lightwindow.service.CoreService
import com.qingcheng.lightwindow.util.SharedPreferencesUtil
/**
 * 主界面悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled")
class MainView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.main, null)) {
    var stopService = {}
    private val jsInterfaceName = "Android"

    init {
        applyParams {
            width = 350f.toIntDip()
            height = 620f.toIntDip()
        }
        applyView {
            findViewById<ImageView>(R.id.iv_main_close).setOnClickListener { zoomOut() }
            findViewById<WebView>(R.id.wv_main).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("main版本", it)
                            SharedPreferencesUtil.put(
                                context,
                                CacheName.CACHE_MAIN_VERSION.keyName,
                                it
                            )
                        }
                    }
                }
                webChromeClient=object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(this@MainView::class.qualifiedName, "${message()} -- From line ${lineNumber()} of ${sourceId()}")
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                addJavascriptInterface(JsInterface(context), jsInterfaceName)
                loadUrl("https://qingcheng.asia/main/")
            }
        }
    }

    fun zoomIn() {
        view.scaleX = 0f
        view.scaleY = 0f
        addToWindow()
        view.animate().scaleX(1f).scaleY(1f).start()
    }

    fun zoomOut() {
        view.animate().scaleX(0f).scaleY(0f).withEndAction {
            view.visibility=View.GONE
            view.findViewById<WebView>(R.id.wv_main).destroy()
            removeFromWindow()
            stopService()
        }
    }
    class JsInterface(private val context: Context) {

        @JavascriptInterface
        fun startService() {
            context.startForegroundService(Intent(context,CoreService::class.java))
        }

        @JavascriptInterface
        fun stopService() {
            context.stopService(Intent(context,CoreService::class.java))
        }
        @JavascriptInterface
        fun isRunning():Boolean{
            val manager=context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
            val list=manager.getRunningServices(10)
            for (s in list){
                if (s.service.className==CoreService::class.qualifiedName)return true
            }
            return false
        }
    }
}