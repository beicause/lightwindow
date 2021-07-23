package com.qingcheng.floatwindow.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.qingcheng.floatwindow.R
import com.qingcheng.floatwindow.util.ScreenDisplayUtil

@SuppressLint("ClickableViewAccessibility")
class ZoomView(context: Context) :
    BaseDragView<View>(context, View.inflate(context, R.layout.zoom, null)) {
    init {
        var animator: ValueAnimator
        applyParams {
            width = 200f.toDip().toInt()
            height = width
        }
        applyView {
            findViewById<ImageView>(R.id.iv_zoom_drag).setOnTouchListener { _, _ ->
                val runnable = Runnable {
                    ViewManager.get(CalendarView::class).apply {
                        applyParams {
                            width = 350f.toIntDip()
                            height = (350 / 0.618f).toIntDip()
                            if (ScreenDisplayUtil.isLandscape(context)) {
                                val t = width
                                width = height
                                height = t
                            }
                        }
                        updateView()
                    }
                }
                handler.postDelayed(runnable,2000)
                onActionUp = {
                    handler.removeCallbacks(runnable)
                    ViewManager.get(CalendarView::class).view.findViewById<ImageView>(R.id.iv_zoom).visibility =
                        View.VISIBLE
                    this@ZoomView.removeFromWindow()
                    onActionUp = {}
                }
                onActionMove = {
                    handler.removeCallbacks(runnable)
                    onActionUp = {}
                }
                return@setOnTouchListener false
            }
            findViewById<ImageView>(R.id.iv_up_h).setOnTouchListener { _, _ ->
                ViewManager.get(CalendarView::class).apply {
                    animator =
                        ValueAnimator.ofInt(view.height, ScreenDisplayUtil.getHeight(context))
                            .apply {
                                addUpdateListener {
                                    applyParams { height = it.animatedValue as Int }
                                    updateView()
                                }
                                duration = (ScreenDisplayUtil.getHeight(context) - view.height) * 3L
                                start()
                            }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }
            findViewById<ImageView>(R.id.iv_down_h).setOnTouchListener { _, _ ->
                ViewManager.get(CalendarView::class).apply {
                    animator = ValueAnimator.ofInt(view.height, 200f.toDip().toInt()).apply {
                        addUpdateListener {
                            applyParams { height = it.animatedValue as Int }
                            updateView()
                        }
                        duration = (view.height - 200f.toDip().toInt()) * 3L
                        start()
                    }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }
            findViewById<ImageView>(R.id.iv_up_v).setOnTouchListener { _, _ ->
                ViewManager.get(CalendarView::class).apply {
                    animator =
                        ValueAnimator.ofInt(view.width, ScreenDisplayUtil.getWidth(context)).apply {
                            addUpdateListener {
                                applyParams { width = it.animatedValue as Int }
                                updateView()
                            }
                            duration = (ScreenDisplayUtil.getWidth(context) - view.width) * 3L
                            start()
                        }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }

            findViewById<ImageView>(R.id.iv_down_v).setOnTouchListener { _, _ ->
                ViewManager.get(CalendarView::class).apply {
                    animator = ValueAnimator.ofInt(view.width, 200f.toDip().toInt()).apply {
                        addUpdateListener {
                            applyParams { width = it.animatedValue as Int }
                            updateView()
                        }
                        duration = (view.width - 200f.toDip().toInt()) * 3L
                        start()
                    }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }
        }
    }
}