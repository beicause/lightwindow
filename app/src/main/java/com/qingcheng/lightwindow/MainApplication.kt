package com.qingcheng.lightwindow

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import com.qingcheng.lightwindow.MainApplication.Companion.toast

class MainApplication : Application() {
    companion object{
        var toast: Toast? = null
    }

    @SuppressLint("ShowToast")
    override fun onCreate() {
        super.onCreate()
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

    }


}

/**
 * 全局toast，位置稍偏下防止被悬浮窗遮挡
 * @param msg 吐司文本内容
 * @param isLong 是否是长时间的吐司
 * */
fun showToast(msg: String, isLong: Boolean = false) {
    Handler(Looper.getMainLooper()).post {
        toast?.apply {
            setText(msg)
            setGravity(Gravity.BOTTOM,0,30)
            duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            show()
        }
    }
}