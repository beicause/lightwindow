package com.qingcheng.floatwindow.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.ImageView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.qingcheng.floatwindow.R
import com.qingcheng.floatwindow.util.CacheName.CACHE_MAIN_HEIGHT
import com.qingcheng.floatwindow.util.CacheName.CACHE_MAIN_WIDTH
import com.qingcheng.floatwindow.util.ScreenDisplayUtil
import com.qingcheng.floatwindow.util.SharedPreferencesUtil

@SuppressLint("SetJavaScriptEnabled")
class CalendarView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.calendar, null)) {
    private val jsInterfaceName = "android"
    var rotateOutListener = {}

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(context, CACHE_MAIN_WIDTH) == 0) 350f.toIntDip()
                else SharedPreferencesUtil.getInt(context, CACHE_MAIN_WIDTH)
            height =
                if (SharedPreferencesUtil.getInt(context, CACHE_MAIN_HEIGHT) == 0)
                    (350 / 0.618f).toIntDip()
                else SharedPreferencesUtil.getInt(context, CACHE_MAIN_HEIGHT)
        }
        applyView {
            findViewById<WebView>(R.id.webview).apply {
                settings.javaScriptEnabled = true
                loadUrl("file:///android_asset/index/index.html")
                addJavascriptInterface(JsInterface(context), jsInterfaceName)
            }
            findViewById<ImageView>(R.id.iv_close).setOnClickListener {
                applyParams {
                    SharedPreferencesUtil.put(context, CACHE_MAIN_WIDTH, width)
                    SharedPreferencesUtil.put(context, CACHE_MAIN_HEIGHT, height)
                }
                rotateOut()
            }
            findViewById<ImageView>(R.id.iv_band).setOnClickListener {
                if (!ViewManager.get(GuideView::class).isAddToWindow) {
                    ViewManager.get(GuideView::class).addToWindow()
                    ViewManager.get(BookView::class).apply {
                        addToWindow()
                        moveTo(
                            toX = this@CalendarView.centerLeftBottom.first,
                            toY = this@CalendarView.centerLeftBottom.second - 50f.toIntDip()
                        )
                    }
                    ViewManager.get(MusicView::class).apply {
                        addToWindow()
                        moveTo(
                            toX = this@CalendarView.centerLeftBottom.first + 50f.toIntDip(),
                            toY = this@CalendarView.centerLeftBottom.second- 50f.toIntDip()
                        )
                    }
                } else (ViewManager.get(
                    GuideView::class,
                    BookView::class,
                    MusicView::class
                )).forEach {
                    it.removeFromWindow()
                }
            }
            findViewById<ImageView>(R.id.iv_zoom).setOnClickListener {
                it.visibility = View.GONE
                ViewManager.new(::ZoomView, context).apply {
                    addToWindow()
                    view.post{
                        moveTo(
                            toX = (ScreenDisplayUtil.getWidth(context) - view.width) / 2,
                            toY = (ScreenDisplayUtil.getHeight(context) - view.height) / 2
                        )
                    }
                }
            }
        }
    }

    fun rotateIn() {
        applyView {
            visibility = View.VISIBLE
            rotationY = 90f
            addToWindow()
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 0f).apply {
                spring.stiffness = SpringForce.STIFFNESS_LOW
                start()
            }
        }
    }

    fun rotateOut() {
        applyView {
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 90f).apply {
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
                addEndListener { _, _, _, _ ->
                    visibility = View.INVISIBLE
                    this@CalendarView.removeFromWindow()
                    rotateOutListener()
                }
            }
        }
    }

    class JsInterface(private val context: Context) {
        @JavascriptInterface
        fun getCache(): String {
            return SharedPreferencesUtil.getAllData(context)
        }
    }
}