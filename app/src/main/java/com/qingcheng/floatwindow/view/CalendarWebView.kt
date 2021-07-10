package com.qingcheng.floatwindow.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.Button
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.qingcheng.floatwindow.R
import com.qingcheng.floatwindow.model.FloatViewModel
import com.qingcheng.floatwindow.util.ScreenDisplayUtil

@SuppressLint("SetJavaScriptEnabled")
class CalendarWebView private constructor(context: Context) :
    FloatViewModel<View>(context, View.inflate(context, R.layout.wv_calendar, null)) {
    companion object {
        private var instance:CalendarWebView?=null
        fun getInstance(context: Context?=null): CalendarWebView? {
            if (instance == null && context!=null) instance = CalendarWebView(context)
            return instance
        }
    }
    init {
        applyView {
            with(findViewById<WebView>(R.id.webview)) {
                setLayerType(View.LAYER_TYPE_SOFTWARE,null)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.builtInZoomControls = false
//                webViewClient = object : WebViewClient() { override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean { request?.let { if (request.url.toString().startsWith("http:") || request.url.toString().startsWith("https:")) return false } val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))context.startActivity(intent)return true } }
//                addJavascriptInterface(RingtoneInterface(context), "android_ringtone")
//                addJavascriptInterface(VibratorInterface(context), "android_vibrator")
//                addJavascriptInterface(WebViewInterface(), "android_webview")
                loadUrl("file:///android_asset/index/index.html")
            }
            findViewById<Button>(R.id.btn_close).setOnClickListener {
                rotateOut()
            }
        }
        applyParams {
            width = ScreenDisplayUtil.getWidth(context) - 50
            height = 1800
            gravity = Gravity.CENTER
        }
    }

    fun rotateIn() {
        applyView {
            visibility=View.VISIBLE
            rotationY=90f
            addToWindow()
            Handler(Looper.getMainLooper()).postDelayed({
                SpringAnimation(this, DynamicAnimation.ROTATION_Y, 0f).apply {
                    spring.stiffness = SpringForce.STIFFNESS_LOW
                    start()
                }
            }, 500)
        }
    }

    fun rotateOut() {
        applyView {
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 90f).apply {
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
                addEndListener { _, _, _, _ ->
                    visibility=View.INVISIBLE
                    this@CalendarWebView.removeFromWindow()
                }
            }
        }
    }

//    private class RingtoneInterface(private val context: Context) {
//        @JavascriptInterface
//        fun ring() {
//            RingtoneUtil.ring(context)
//        }
//
//        @JavascriptInterface
//        fun stop() {
//            RingtoneUtil.stop()
//        }
//    }
//
//    private class VibratorInterface(private var context: Context) {
//        @JavascriptInterface
//        fun vibrate() {
//            VibratorUtil.vibrate(context)
//        }
//
//        @JavascriptInterface
//        fun cancel() {
//            VibratorUtil.cancel()
//        }
//    }
//
//    private class WebViewInterface {
//        @JavascriptInterface
//        fun close() {
//        }
//    }
}