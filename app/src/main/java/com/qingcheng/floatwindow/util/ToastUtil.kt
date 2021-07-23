package com.qingcheng.floatwindow.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object ToastUtil {
    var context:Context?=null
    private var toast:Toast?=null
    fun show( text:String, isLong:Boolean=false,context: Context?=null,){
        if (context==null)this.context=context
        if (toast==null) toast= Toast(context)
        toast?.apply {
            setText(text)
            duration=if (isLong)Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            show()
        }
    }
}