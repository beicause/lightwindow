package com.qingcheng.baseutil.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.qingcheng.baseutil.util.ScreenDisplayUtil

/**
 * 可拖拽悬浮窗基类
 * */
@SuppressLint("ClickableViewAccessibility")
open class  BaseDragView<T:View> (context: Context,view: T) :
    BaseFloatWindow<T>(context, view) {

    private var lastX = 0f
    private var lastY = 0f
    private var isDragged = false
    var enableDrag=true
    var onActionUp={}
    var onActionMove={}
    init {
        applyParams {
            gravity=Gravity.START or Gravity.TOP
            flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        }
        applyView {
            setOnTouchListener { _, event ->
                if (!enableDrag)return@setOnTouchListener false
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragged=false
                        lastX = event.rawX
                        lastY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - lastX
                        val dy = event.rawY - lastY
                        if (isDragged || dx * dx + dy * dy > 81) {
                            isDragged = true
                            onActionMove()
                            applyParams {
                                var tx = x + dx
                                var ty = y + dy
                                tx = when {
                                    tx < 0 -> 0f
                                    tx > ScreenDisplayUtil.getWidth(context) - view.width -> (ScreenDisplayUtil.getWidth(
                                        context
                                    ) - view.width).toFloat()
                                    else -> tx
                                }
                                ty = when {
                                    ty < 0 -> 0f
                                    ty > ScreenDisplayUtil.getHeight(context) - view.height -> (ScreenDisplayUtil.getHeight(
                                        context
                                    ) - view.height).toFloat()
                                    else -> ty
                                }
                                x = tx.toInt()
                                y = ty.toInt()
                            }
                            updateView()
                        }
                        lastX=event.rawX
                        lastY=event.rawY
                    }
                    MotionEvent.ACTION_UP->{
                        onActionUp()
                    }
                }
                return@setOnTouchListener isDragged
            }
        }
    }
}