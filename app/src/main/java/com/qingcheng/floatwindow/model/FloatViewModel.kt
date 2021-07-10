package com.qingcheng.floatwindow.model

import android.app.Service
import android.content.Context
import android.view.View
import android.view.WindowManager

open class FloatViewModel<T : View>(context: Context, val view: T) {
    var isAddToWindow: Boolean = false
        private set
    private val params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }
    private val manager: WindowManager =
        context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    fun applyView(block: T.() -> Unit) {
        block(view)
    }

    fun applyParams(block: WindowManager.LayoutParams.() -> Unit) {
        block(params)
    }

    fun addToWindow() {
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
    }

    fun updateView(){
        if (isAddToWindow) manager.updateViewLayout(view, params)
    }
    fun removeFromWindow() {
        if (isAddToWindow) manager.removeView(view)
        isAddToWindow = false
    }
}
