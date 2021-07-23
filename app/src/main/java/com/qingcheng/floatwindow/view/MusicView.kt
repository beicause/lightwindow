package com.qingcheng.floatwindow.view

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import com.qingcheng.floatwindow.R

class MusicView(context: Context) :
    BaseDragView<ImageView>(context,ImageView(context)) {

    init {
        applyParams {
            width = 50f.toIntDip()
            height = width
            gravity = Gravity.START or Gravity.TOP
        }
        applyView {
            setImageResource(R.drawable.music)
        }
    }
}