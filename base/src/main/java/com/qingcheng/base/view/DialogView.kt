package com.qingcheng.base.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.qingcheng.base.MAIN_HEIGHT
import com.qingcheng.base.MAIN_WIDTH
import com.qingcheng.base.R
import com.qingcheng.base.util.SharedPreferencesUtil

class DialogView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.dialog_layout, null)) {
    var maskClick = {}
        set(value) {
            field = value
            view.findViewById<ConstraintLayout>(R.id.cl_dialog_container)
                .setOnClickListener { value() }
        }
    var title = ""
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_title).text = value
        }
    var content = ""
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_content).text = value
        }
    var cancelText = ""
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_cancel).apply {
                text = value
                if (value == "") visibility = View.INVISIBLE
            }
        }
    var confirmText = ""
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_confirm).apply {
                text = value
                if (value == "") visibility = View.INVISIBLE
            }
        }
    var cancelClick = {}
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { value() }
        }
    var confirmClick = {}
        set(value) {
            field = value
            view.findViewById<TextView>(R.id.tv_confirm).setOnClickListener { value() }
        }

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(
                        context,
                        MAIN_WIDTH
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_WIDTH)
            height =
                if (SharedPreferencesUtil.getInt(context, MAIN_HEIGHT) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_HEIGHT)
        }
        title = ""
        content = ""
        cancelText = "取消"
        confirmText = "确定"
        cancelClick = { zoomOut() }
        confirmClick = { zoomOut() }
        maskClick = { zoomOut() }
    }

    override fun zoomIn() {
        view.findViewById<ConstraintLayout>(R.id.cl_dialog).apply {
            scaleX = 0f
            scaleY = 0f
            addToWindow()
            animate().scaleX(1f).scaleY(1f).start()
        }
    }

    override fun zoomOut(onEnded: () -> Unit) {
        view.findViewById<ConstraintLayout>(R.id.cl_dialog).apply {
            animate().scaleX(0f).scaleY(0f).withEndAction {
                visibility = View.GONE
                removeFromWindow()
                onEnded()
            }
        }
    }
}