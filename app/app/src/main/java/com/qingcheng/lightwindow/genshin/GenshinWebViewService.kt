package com.qingcheng.lightwindow.genshin

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import com.qingcheng.base.INDEX_URL
import com.qingcheng.base.JS_INTERFACE_NAME
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.BaseWebView
import com.qingcheng.base.view.DragAbility
import com.qingcheng.base.view.ViewManager
import com.qingcheng.lightwindow.R

class GenshinWebViewService : Service() {
    private lateinit var floatView: BaseWebView
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()
        floatView =
            ViewManager.new<BaseWebView>(BaseWebView(this), "Genshin").apply {
                DragAbility.enable(this,null,view.findViewById(R.id.webview))
                view.setBackgroundColor(0)
                view.findViewById<WebView>(R.id.webview).apply {
                    setBackgroundColor(0)
                    settings.builtInZoomControls = false
                }
                applyParams {
                    gravity = Gravity.TOP or Gravity.START
                    flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    width = 100f.toIntDip()
                    height = 150f.toIntDip()
                    setPosition(
                        ScreenUtil.getWidth(this@GenshinWebViewService) - width,
                        ScreenUtil.getHeight(this@GenshinWebViewService) - height
                    )
                    if (ScreenUtil.isLandscape(this@GenshinWebViewService)) {
                        val t = width
                        width = height
                        height = t
                    }
                }
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = "$INDEX_URL/genshin/"
        floatView.apply {
            loadUrl(url)
            addToWindow()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        ViewManager.destroy("Genshin")
    }
}
