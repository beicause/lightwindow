package com.qingcheng.baseutil.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object ToastUtil {
    var context: Context? = null
        @SuppressLint("ShowToast")
        set(value) {
            field = value
            toast = if (value==null) null
            else Toast.makeText(value, "", Toast.LENGTH_LONG)
        }
    var toast: Toast? = null
    var toastMethod: (() -> Unit)? = null
    fun showToast(
        text: CharSequence,
        toastMethod: (() -> Unit)? = null,
        context: Context? = null,
        isLong: Boolean = false
    ) {
        if (toastMethod != null) {
            toastMethod()
            return
        }
        if (context != null) this.context = context
        if (toast == null) throw NullPointerException("toast is not initialized, please set the context")
        if (isLong) toast!!.duration = Toast.LENGTH_LONG
        else toast!!.duration = Toast.LENGTH_SHORT
        toast!!.setText(text)
        toast!!.show()

    }
}