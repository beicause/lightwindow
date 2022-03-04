package com.qingcheng.base.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.qingcheng.base.util.ScreenUtil

interface DragCallback {
    var onActionUp: () -> Unit
    var onActionMove: () -> Unit
}

/**
 * 可拖拽悬浮窗基类
 * */
@SuppressLint("ClickableViewAccessibility")
object DragAbility {

    fun enable(
        window: BaseFloatWindow<*>,
        dragCallback: DragCallback? = null,
        v: View = window.view
    ): (Boolean) -> Unit {
        var enableDrag = true
        window.apply {
            var lastX = 0f
            var lastY = 0f
            var isDragged = false

            applyParams {
                gravity = Gravity.START or Gravity.TOP
                flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            }
            v.apply {
                setOnTouchListener { _, event ->
                    Log.i("","bbb")
                    if (!enableDrag) return@setOnTouchListener false
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            isDragged = false
                            lastX = event.rawX
                            lastY = event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.rawX - lastX
                            val dy = event.rawY - lastY
                            if (isDragged || dx * dx + dy * dy > 81) {
                                isDragged = true
                                dragCallback?.onActionMove?.invoke()
                                applyParams {
                                    var tx = x + dx
                                    var ty = y + dy
                                    tx = when {
                                        tx < 0 -> 0f
                                        tx > ScreenUtil.getWidth(context) - view.width -> (ScreenUtil.getWidth(
                                            context
                                        ) - view.width).toFloat()
                                        else -> tx
                                    }
                                    ty = when {
                                        ty < 0 -> 0f
                                        ty > ScreenUtil.getHeight(context) - view.height -> (ScreenUtil.getHeight(
                                            context
                                        ) - view.height).toFloat()
                                        else -> ty
                                    }
                                    x = tx.toInt()
                                    y = ty.toInt()
                                }
                                updateView()
                            }
                            lastX = event.rawX
                            lastY = event.rawY
                        }
                        MotionEvent.ACTION_UP -> {
                            dragCallback?.onActionUp?.invoke()
                        }
                    }
                    return@setOnTouchListener isDragged
                }
            }
        }
        return { enable -> enableDrag = enable }
    }
}
