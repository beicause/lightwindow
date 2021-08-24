package com.qingcheng.lightwindow.util

import android.app.Service
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import java.util.*

object VibratorUtil {
    private var vibrator: Vibrator? = null
    fun vibrate(context: Context) {
        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                300000,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    fun cancel() {
        vibrator?.cancel()
        vibrator = null
    }

    fun vibrate(context: Context, ms: Long) {
        vibrate(context)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                vibrator?.cancel()
            }
        }, ms)
    }
}