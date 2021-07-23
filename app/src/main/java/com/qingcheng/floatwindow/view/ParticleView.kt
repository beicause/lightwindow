package com.qingcheng.floatwindow.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout

class ParticleView (private val context: Context,private val resId:Int) :
    BaseFloatWindow<LinearLayout>(context, LinearLayout(context).apply {setBackgroundColor(Color.WHITE)
    }) {
    private val childView: View=View(context)
    init {
        applyView {
            addView(childView,WindowManager.LayoutParams().apply { width=1000f.toDip().toInt();height=width })
        }
        applyParams {
            width=1000f.toDip().toInt()
            height=width
            flags= flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
    }

}