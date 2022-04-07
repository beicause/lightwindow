package com.qingcheng.lightwindow.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.*
import com.qingcheng.lightwindow.R


/**
 * 点击缩放后打开的悬浮窗，具有调整日程表大小的功能
 * */
@SuppressLint("ClickableViewAccessibility")
class ZoomView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.zoom, null)), DragCallback {

    override var onActionUp = {}
    override var onActionMove = {}

    init {
        DragAbility.enable(this,this)
        var animator: ValueAnimator
        applyParams {
            width = 100f.toDip().toInt()
            height = width
        }
        applyView {
            //长按中心图标2秒恢复默认大小
            findViewById<ImageView>(R.id.iv_zoom_drag).setOnTouchListener { _, _ ->
                ToastUtil.showToast("长按2秒可恢复默认大小")
                val runnable = Runnable {
                    ViewManager.get(UIWebView::class)!!.apply {
                        applyParams {
                            width = 350f.toIntDip()
                            height = 620f.toIntDip()
                            if (ScreenUtil.isLandscape(context)) {
                                val t = width
                                width = height
                                height = t
                            }
                        }
                        updateView()
                    }
                }
                handler.postDelayed(runnable, 2000)
                onActionUp = {
                    handler.removeCallbacks(runnable)
                    ViewManager.get(UIWebView::class)!!.view.findViewById<WebView>(R.id.webview)
                        .evaluateJavascript(
                            "javascript:showZoom()", null
                        )
                    this@ZoomView.removeFromWindow()
                    onActionUp = {}
                }
                onActionMove = {
                    handler.removeCallbacks(runnable)
                    onActionUp = {}
                }
                return@setOnTouchListener false
            }
            //水平放大
            findViewById<ImageView>(R.id.iv_up_h).setOnTouchListener { _, _ ->
                ViewManager.get(UIWebView::class)!!.apply {
                    animator =
                        ValueAnimator.ofInt(view.height, ScreenUtil.getHeight(context))
                            .apply {
                                addUpdateListener {
                                    applyParams { height = it.animatedValue as Int }
                                    updateView()
                                }
                                duration = (ScreenUtil.getHeight(context) - view.height) * 3L
                                start()
                            }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }
            //水平缩小
            findViewById<ImageView>(R.id.iv_down_h).setOnTouchListener { _, _ ->
                ViewManager.get(UIWebView::class)!!.apply {
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
            //垂直放大
            findViewById<ImageView>(R.id.iv_up_v).setOnTouchListener { _, _ ->
                ViewManager.get(UIWebView::class)!!.apply {
                    animator =
                        ValueAnimator.ofInt(view.width, ScreenUtil.getWidth(context)).apply {
                            addUpdateListener {
                                applyParams { width = it.animatedValue as Int }
                                updateView()
                            }
                            duration = (ScreenUtil.getWidth(context) - view.width) * 3L
                            start()
                        }
                }
                onActionUp = { animator.cancel() }
                onActionMove = { animator.cancel() }
                return@setOnTouchListener false
            }
            //垂直缩小
            findViewById<ImageView>(R.id.iv_down_v).setOnTouchListener { _, _ ->
                ViewManager.get(UIWebView::class)!!.apply {
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
