package com.qingcheng.lightwindow.util

import android.app.Service
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import java.util.*

object VibratorUtil {
    private var vibrator: Vibrator? = null
    /**
     * 开始振动
     * @param context
     */
    fun vibrate(context: Context) {
        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                300000,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
    /**
     * 振动一定时间
     * @param context
     * @param ms 振动毫秒数
     * */
    fun vibrate(context: Context, ms: Long) {
        vibrate(context)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                vibrator?.cancel()
            }
        }, ms)
    }
    /**
     * 停止振动
     * */
    fun cancel() {
        vibrator?.cancel()
        vibrator = null
    }
}