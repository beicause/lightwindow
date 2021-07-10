package com.qingcheng.floatwindow.util

import android.app.Service
import android.content.Context
import android.os.*

object VibratorUtil {
    private var vibrator: Vibrator? = null
    fun vibrate(context: Context) {
        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    600000,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else vibrator?.vibrate(600000)
    }

    fun cancel() {
        vibrator?.cancel()
    }

    fun vibrate(context: Context, ms: Long) {
        vibrate(context)
        Handler(Looper.getMainLooper()).postDelayed({ cancel() }, ms)
    }

}