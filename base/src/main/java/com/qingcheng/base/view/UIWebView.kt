package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.qingcheng.base.*
import com.qingcheng.base.util.*
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.coroutines.*

/**
 * 主界面悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
class UIWebView(val context: Context, val service: Class<out Service>) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {
    private var isError = false
    private val scope = MainScope()

    init {
        applyParams {
            val w = PreferencesUtil.getString(context, MAIN_WIDTH)
            val h = PreferencesUtil.getString(context, MAIN_HEIGHT)
            width = w?.toInt() ?: 350.toIntDip()
            height = h?.toInt() ?: 620.toIntDip()
        }
        applyView {
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                addJavascriptInterface(BaseJsInterface(context), "${JS_INTERFACE_NAME}Base")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("web版本", it)
                            if (it != "null") {
                                val version = it.replace("\"", "")
                                val localVersion =
                                    PreferencesUtil.getString(
                                        context,
                                        WEB_VERSION
                                    )

                                if (localVersion == null
                                    || localVersion
                                        .toInt() < version.toInt()
                                ) scope.launch {
                                    PreferencesUtil.putString(
                                        context,
                                        WEB_VERSION,
                                        version
                                    )
                                }
                            }
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        if (error?.errorCode == 404) return
                        Log.e(
                            this@UIWebView::class.qualifiedName,
                            "error " + error?.errorCode + error?.description
                        )
                        ToastUtil.showToast("网络异常或省流量模式限制", isLong = true)
                        isError = true
                        super.onReceivedError(view, request, error)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(
                                this@UIWebView::class.qualifiedName,
                                "${message()} -- From line ${lineNumber()} of ${sourceId()}"
                            )
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                isFocusableInTouchMode = true
                requestFocusFromTouch()
                setOnFocusChangeListener { v, _ ->
                    v.requestFocus()
                }
                setOnKeyListener { _, keyCode, event ->
                    Log.i("onKey", keyCode.toString() + "/" + event.action)
                    if (event.action != KeyEvent.ACTION_UP) return@setOnKeyListener false
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        zoomOut { stop() }
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }

    fun loadUrl(url: String) {
        view.findViewById<WebView>(R.id.webview).loadUrl(url)
    }

    private val stop = {
        context.stopService(Intent().apply { setClassName(context, uiWebViewServiceName) })
    }
}