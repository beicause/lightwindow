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
import java.util.*

/**
 * 主界面悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
class UIWebView(context: Context) : BaseWebView(context) {
    private var lastTime = 0L

    init {
        applyParams {
            val w = PreferencesUtil.getString(context, MAIN_WIDTH)
            val h = PreferencesUtil.getString(context, MAIN_HEIGHT)
            width = w?.toInt() ?: 350.toIntDip()
            height = h?.toInt() ?: 620.toIntDip()
        }
        applyView {
            findViewById<WebView>(R.id.webview).apply {
                isFocusableInTouchMode = true
                requestFocusFromTouch()
                setOnFocusChangeListener { v, _ ->
                    v.requestFocus()
                }
                setOnKeyListener { _, keyCode, event ->
                    Log.i("onKey", keyCode.toString() + "/" + event.action)
                    if (event.action != KeyEvent.ACTION_UP) return@setOnKeyListener false
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (url?.matches(Regex(""".*(calendar|main).*""")) == false && canGoBack()) goBack()
                        else {
                            val time = Date().time
                            if (time - lastTime < 2000) zoomOut { stop() }
                            else {
                                ToastUtil.showToast("再按一次退出")
                                lastTime = time
                            }
                        }
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }

    private val stop = {
        context.stopService(Intent().apply { setClassName(context, uiWebViewServiceName) })
    }
}
