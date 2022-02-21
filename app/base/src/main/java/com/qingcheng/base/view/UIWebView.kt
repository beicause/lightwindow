package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.webkit.WebViewAssetLoader
import com.qingcheng.base.*
import com.qingcheng.base.util.*
import kotlinx.coroutines.*

/**
 * 主界面悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
class UIWebView(val context: Context, val service: Class<out Service>) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {
    private val scope = MainScope()

    init {
        applyParams {
            val w = PreferencesUtil.getString(context, MAIN_WIDTH)
            val h = PreferencesUtil.getString(context, MAIN_HEIGHT)
            width = w?.toInt() ?: 350.toIntDip()
            height = h?.toInt() ?: 620.toIntDip()
        }
        applyView {
            val assetLoader =
                WebViewAssetLoader.Builder().setHttpAllowed(true).setDomain(DOMAIN).addPathHandler(
                    "/",
                    WebViewAssetLoader.AssetsPathHandler(context)
                ).build()
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
//                addJavascriptInterface(BaseJsInterface(context), "${JS_INTERFACE_NAME}Base")
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        return request?.let {
                            if (it.url.host == DOMAIN)
                                assetLoader.shouldInterceptRequest(
                                    when {
                                        it.url.lastPathSegment?.contains(
                                            '.'
                                        ) == true -> it.url
                                        it.url.toString().contains("/calendar//") -> {
                                            Uri.parse(
                                                "$CALENDAR_URL/index.html"
                                            )
                                        }
                                        else -> Uri.parse("$INDEX_URL/index.html")
                                    }
                                )
                            else null
                        } ?: super.shouldInterceptRequest(view, request)
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