package com.qingcheng.floatwindow.view

import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.qingcheng.floatwindow.util.ScreenDisplayUtil

open class BaseFloatWindow<T : View>(private val context: Context, val view: T) {

    private val manager: WindowManager =
        context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    var isAddToWindow: Boolean = false
        private set
    val params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }
    val centerLeftBottom by lazy {
        Pair(
            (ScreenDisplayUtil.getWidth(context) - view.width) / 2,
            (ScreenDisplayUtil.getHeight(context) + view.height) / 2
        )
    }
    val centerLeftTop by lazy {
        Pair(
            (ScreenDisplayUtil.getWidth(context) - view.width) / 2,
            (ScreenDisplayUtil.getHeight(context) - view.height) / 2
        )
    }
    val centerRightBottom by lazy {
        Pair(
            (ScreenDisplayUtil.getWidth(context) + view.width) / 2,
            (ScreenDisplayUtil.getHeight(context) - view.height) / 2
        )
    }
    val centerRightTop by lazy {
        Pair(
            (ScreenDisplayUtil.getWidth(context) + view.width) / 2,
            (ScreenDisplayUtil.getHeight(context) + view.height) / 2
        )
    }

    inline fun applyView(block: T.() -> Unit) {
        block(view)
    }

    inline fun applyParams(block: WindowManager.LayoutParams.() -> Unit) {
        block(params)
    }

    open fun addToWindow() {
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
    }

    fun addToWindow(callback: () -> Unit = {}) {
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
        callback()
    }

    fun updateView() {
        if (isAddToWindow) manager.updateViewLayout(view, params)
    }

    fun removeFromWindow(callback: () -> Unit = {}) {
        if (isAddToWindow) manager.removeView(view)
        isAddToWindow = false
        callback()
    }

    fun Float.toDip(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )

    fun Float.toIntDip(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()

    fun moveTo(toX: Int, toY: Int, fromX: Int? = null, fromY: Int? = null, duration: Long? = null) {
        if (!isAddToWindow) return
        ValueAnimator.ofInt(fromX ?: view.x.toInt(), toX).apply {
            addUpdateListener {
                params.x = it.animatedValue as Int
                updateView()
            }
            if (duration != null) setDuration(duration)
            start()
        }
        ValueAnimator.ofInt(fromY ?: view.y.toInt(), toY).apply {
            addUpdateListener {
                params.y = it.animatedValue as Int
                updateView()
            }
            if (duration != null) setDuration(duration)
            start()
        }
    }

    fun addToWindow(x: Int, y: Int, callback: () -> Unit = {}) {
        setPosition(x, y)
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
        callback()
    }

    fun setPosition(x: Int, y: Int) {
        params.x = x
        params.y = y
        updateView()
    }
}
