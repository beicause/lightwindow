package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.webkit.WebViewAssetLoader
import com.qingcheng.base.*

@SuppressLint("SetJavaScriptEnabled")
open class BaseWebView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {

    init {
        applyView {
            val assetLoader =
                WebViewAssetLoader.Builder().setHttpAllowed(true).setDomain(DOMAIN).addPathHandler(
                    "/",
                    WebViewAssetLoader.AssetsPathHandler(context)
                ).build()
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        return request?.let {
                            when {
                                (it.url.lastPathSegment == "version.json") -> null
                                (it.url.host == DOMAIN) ->
                                    assetLoader.shouldInterceptRequest(
                                        when {
                                            it.url.lastPathSegment?.contains(".") == true -> it.url
                                            it.url.toString().contains("/calendar//") -> Uri.parse(
                                                "$CALENDAR_URL/index.html"
                                            )
                                            it.url.toString()
                                                .contains("/genshin/") -> Uri.parse("$INDEX_URL/genshin/index.html")
                                            else -> Uri.parse("$INDEX_URL/index.html")
                                        }

                                    )
                                else -> null
                            }
                        } ?: super.shouldInterceptRequest(view, request)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(
                                this@BaseWebView::class.simpleName,
                                "${message()} -- From line ${lineNumber()} of ${sourceId()}"
                            )
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
            }
        }
    }

    fun loadUrl(url: String) {
        view.findViewById<WebView>(R.id.webview).loadUrl(url)
    }
}
