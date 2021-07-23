package com.qingcheng.floatwindow.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.qingcheng.floatwindow.R

@SuppressLint("ClickableViewAccessibility")
class BookView(context: Context) :
    BaseDragView<ImageView>(context, ImageView(context)) {

    init {
        applyParams {
            width = 50f.toIntDip()
            height = width
        }
        applyView {
            setImageResource(R.drawable.book)
            var f=false
            setOnClickListener{
                f=!f
                if (f)setImageResource(R.drawable.book_close)
                else setImageResource(R.drawable.book)
            }
        }
    }
}