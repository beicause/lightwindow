package com.qingcheng.base.view

import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.qingcheng.base.util.ScreenUtil

/**
 * 构建悬浮窗的基类
 * */
open class BaseFloatWindow<T : View>(private val context: Context, val view: T) {

    private val manager: WindowManager =
        context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    var isAddToWindow: Boolean = false
        private set
    val params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        format = PixelFormat.RGBA_8888
    }

    //<editor-fold desc="如果悬浮窗在中央，那么下面四个坐标是有用的">
    val centerLeftBottom by lazy {
        Pair(
            (ScreenUtil.getWidth(context) - view.width) / 2,
            (ScreenUtil.getHeight(context) + view.height) / 2
        )
    }
    val centerLeftTop by lazy {
        Pair(
            (ScreenUtil.getWidth(context) - view.width) / 2,
            (ScreenUtil.getHeight(context) - view.height) / 2
        )
    }
    val centerRightBottom by lazy {
        Pair(
            (ScreenUtil.getWidth(context) + view.width) / 2,
            (ScreenUtil.getHeight(context) - view.height) / 2
        )
    }
    val centerRightTop by lazy {
        Pair(
            (ScreenUtil.getWidth(context) + view.width) / 2,
            (ScreenUtil.getHeight(context) + view.height) / 2
        )
    }
    //</editor-fold>

    /**
     * 对view进行操作
     * @param block
     * */
    inline fun applyView(block: T.() -> Unit) {
        block(view)
    }

    /**
     * 对布局参数进行操作
     * @param block
     * */
    inline fun applyParams(block: WindowManager.LayoutParams.() -> Unit) {
        block(params)
    }

    /**
     * 添加到窗口上，不会重复添加
     * */
    fun addToWindow() {
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
    }

    /**
     * 添加到窗口上，不会重复添加
     * @param callback 添加后的回调
     * */
    fun addToWindow(callback: () -> Unit = {}) {
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
        callback()
    }

    /**
     * 更新视图
     * */
    fun updateView() {
        if (isAddToWindow) manager.updateViewLayout(view, params)
    }

    /**
     * 从窗口中移除悬浮窗
     * @param callback 移除后的回调
     * */
    fun removeFromWindow(callback: () -> Unit = {}) {
        if (isAddToWindow) manager.removeView(view)
        isAddToWindow = false
        callback()
    }

    fun addToWindow(x: Int, y: Int, callback: () -> Unit = {}) {
        setPosition(x, y)
        if (!isAddToWindow) manager.addView(view, params)
        isAddToWindow = true
        callback()
    }

    fun zoomIn() {
        view.scaleX = 0f
        view.scaleY = 0f
        addToWindow()
        view.animate().scaleX(1f).scaleY(1f).start()
    }

    fun zoomOut(onEnded: () -> Unit = {}) {
        view.animate().scaleX(0f).scaleY(0f).withEndAction {
            view.visibility = View.GONE
            removeFromWindow()
            onEnded()
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

    fun rotateOut(onEnded: () -> Unit = {}) {
        applyView {
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 90f).apply {
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
                addEndListener { _, _, _, _ ->
                    visibility = View.INVISIBLE
                    removeFromWindow()
                    onEnded()
                }
            }
        }
    }

    fun setPosition(x: Int, y: Int) {
        params.x = x
        params.y = y
        updateView()
    }

    fun moveTo(toX: Int, toY: Int, fromX: Int? = null, fromY: Int? = null, duration: Long? = null) {
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

    fun Int.toDip(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )

    fun Int.toIntDip(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()


}
