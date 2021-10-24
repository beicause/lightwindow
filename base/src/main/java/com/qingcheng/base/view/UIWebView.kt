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
import com.qingcheng.base.*
import com.qingcheng.base.util.*
import com.qingcheng.base.util.PreferencesUtil.not
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
@SuppressLint("SetJavaScriptEnabled")
class UIWebView(val context: Context, val service: Class<out Service>) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.webview, null)) {
    private var isError = false
    private var url: String? = null
    private val scope = MainScope()

    init {
        applyParams {
            val w = PreferencesUtil.getString(context, MAIN_WIDTH)
            val h = PreferencesUtil.getString(context, MAIN_HEIGHT)
            width = w?.toInt() ?: 350.toIntDip()
            height = h?.toInt() ?: 620.toIntDip()
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
                                hideLoad()
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
            }
        }
    }

    fun loadUrl(url: String) {
        this.url = url
        view.findViewById<WebView>(R.id.webview).loadUrl(url)
        showLoad()
    }

    private val runnable = {
        if (view.findViewById<ConstraintLayout>(R.id.cl_loads).animation != null)
            view.findViewById<TextView>(
                R.id.tv_load_tips
            ).apply {
                text = "长时间无响应请点击重试"
                visibility = View.VISIBLE
            }
    }

    fun showLoad() {
        val h = Handler(Looper.getMainLooper())
        h.removeCallbacks(runnable)
        view.findViewById<ConstraintLayout>(R.id.cl_mask).visibility = View.VISIBLE
        view.findViewById<WebView>(R.id.webview).visibility = View.GONE
        view.findViewById<TextView>(R.id.tv_load_tips).apply {
            if (!PreferencesUtil.getString(context, NOT_FIRST)) {
                visibility = View.VISIBLE
                text = "首次加载需要一定时间"
            } else visibility = View.GONE
        }
        val anim = AnimationUtils.loadAnimation(context, R.anim.rotate)
        anim.interpolator = LinearInterpolator()
        view.findViewById<ConstraintLayout>(R.id.cl_loads).apply {
            visibility = View.VISIBLE
            animation = anim
        }
        anim.start()
        h.postDelayed(runnable, 4000)
    }

    fun hideLoad() {
        if (!PreferencesUtil.getString(context, NOT_FIRST)) scope.launch {
            PreferencesUtil.putString(
                context, NOT_FIRST, true.toString()
            )
        }
        view.findViewById<WebView>(R.id.webview).visibility = View.VISIBLE
        view.findViewById<ConstraintLayout>(R.id.cl_mask).visibility = View.GONE
        view.findViewById<TextView>(R.id.tv_load_tips).visibility = View.GONE
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