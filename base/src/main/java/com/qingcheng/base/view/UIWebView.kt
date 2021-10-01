package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.postDelayed
import com.qingcheng.base.MAIN_HEIGHT
import com.qingcheng.base.MAIN_WIDTH
import com.qingcheng.base.R
import com.qingcheng.base.WEB_VERSION
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
class UIWebView(val context: Context, val service: Class<out Service>) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {
    private var isError = false
    private var url: String? = null

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(
                        context,
                        MAIN_WIDTH
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_WIDTH)
            height =
                if (SharedPreferencesUtil.getInt(context, MAIN_HEIGHT) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_HEIGHT)
        }
        applyView {
            findViewById<ImageView>(R.id.iv_close).setOnClickListener {
                zoomOut {
                    context.stopService(
                        Intent(
                            context,
                            service
                        )
                    )
                }
            }
            findViewById<ConstraintLayout>(R.id.cl_mask).setOnClickListener {
                isError = false
                if (url == null) isError = true
                else loadUrl(url!!)
            }
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("web版本", it)
                            if (it == "null") isError = true
                            else SharedPreferencesUtil.put(
                                context,
                                WEB_VERSION,
                                it
                            )
                            if (!isError) hideLoad()
                            else url?.let { it1 -> this@UIWebView.loadUrl(it1) }
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
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
            }
        }
    }

    fun loadUrl(url: String) {
        this.url = url
        view.findViewById<WebView>(R.id.webview).loadUrl(url)
        showLoad()
    }

    fun showLoad() {
        view.findViewById<ConstraintLayout>(R.id.cl_mask).visibility = View.VISIBLE
        view.findViewById<WebView>(R.id.webview).visibility = View.GONE
        view.findViewById<TextView>(R.id.tv_reload).visibility = View.GONE
        val anim = AnimationUtils.loadAnimation(context, R.anim.rotate)
        anim.interpolator = LinearInterpolator()
        view.findViewById<ConstraintLayout>(R.id.cl_loads).apply {
            visibility = View.VISIBLE
            animation = anim
        }
        anim.start()
        Handler(Looper.getMainLooper()).postDelayed(2000) {
            if (view.findViewById<ConstraintLayout>(R.id.cl_loads).animation != null)
                view.findViewById<TextView>(
                    R.id.tv_reload
                ).visibility = View.VISIBLE
        }
    }

    fun hideLoad() {
        view.findViewById<WebView>(R.id.webview).visibility = View.VISIBLE
        view.findViewById<ConstraintLayout>(R.id.cl_mask).visibility = View.GONE
        view.findViewById<TextView>(R.id.tv_reload).visibility = View.GONE
        view.findViewById<ConstraintLayout>(R.id.cl_loads).apply {
            if (animation != null) animation.cancel()
            animation = null
        }
    }

//    private fun throwError(message: String = "") {
//        view.findViewById<WebView>(R.id.webview).destroy()
//        this@FloatWebView.view.visibility = View.GONE
//        view.handler.postDelayed({
//            context.stopService(
//                Intent(
//                    context,
//                    service
//                )
//            )
//        }, 3500)
//    }
}