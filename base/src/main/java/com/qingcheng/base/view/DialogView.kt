package com.qingcheng.base.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.qingcheng.base.R
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.util.SharedPreferencesUtil

class DialogView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.dialog_layout, null)) {
    var maskClickAble = true
        set(value) {
            field = value
            if (value)
                view.findViewById<ConstraintLayout>(R.id.cl_dialog_container)
                    .setOnClickListener { zoomOut() }
            else view.findViewById<ConstraintLayout>(R.id.cl_dialog_container)
                .setOnClickListener(null)

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
                        CacheName.MAIN_WIDTH.name
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, CacheName.MAIN_WIDTH.name)
            height =
                if (SharedPreferencesUtil.getInt(context, CacheName.MAIN_HEIGHT.name) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, CacheName.MAIN_HEIGHT.name)
        }
        title = ""
        content = ""
        cancelText = "取消"
        confirmText = "确定"
        cancelClick = { zoomOut() }
        confirmClick = { zoomOut() }
        maskClickAble = true
    }
}