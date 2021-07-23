package com.qingcheng.floatwindow.view

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.qingcheng.floatwindow.R
import com.qingcheng.floatwindow.util.ScreenDisplayUtil
import com.qingcheng.floatwindow.util.TextViewUtil

class GuideView(private val context: Context):
    BaseDragView<View>(context,View.inflate(context,R.layout.guide_lighthouse,null)) {

    val textViewUtil:TextViewUtil = TextViewUtil(view.findViewById(R.id.tv_tips))

    init {
        applyParams {
            width=WindowManager.LayoutParams.WRAP_CONTENT
            height=WindowManager.LayoutParams.WRAP_CONTENT
        }
        applyView {
            setOnClickListener {
                if (textViewUtil.isPrinting)textViewUtil.printToEnd()
                else textViewUtil.printNextText()
            }
        }
    }


    override fun addToWindow() {
        super.addToWindow()
        setPosition(32f.toIntDip(),ScreenDisplayUtil.getHeight(context)- 62f.toIntDip())
        textViewUtil.printNextText()
    }
}