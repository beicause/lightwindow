package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.qingcheng.base.R
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.util.*
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
class FloatWebView(val context: Context, val service: Class<out Service>) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {

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
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("web版本", it)
                            if (it == "null") {
                                FileUtil.deleteDir(context.cacheDir)
                                throwError("页面异常")
                            }
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
                        throwError("检查网络并解除省流量限制")
                        super.onReceivedError(view, request, error)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(
                                this@FloatWebView::class.qualifiedName,
                                "${message()} -- From line ${lineNumber()} of ${sourceId()}"
                            )
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
            }
        }
    }

    private fun throwError(message: String = "") {
        view.findViewById<WebView>(R.id.webview).destroy()
        this@FloatWebView.view.visibility = View.GONE
        ToastUtil.showToast(message, isLong = true)
        view.handler.postDelayed({
            context.stopService(
                Intent(
                    context,
                    service
                )
            )
        }, 3500)
    }
}