package com.qingcheng.floatwindow.util

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper

object RingtoneUtil {
    private var mediaPlayer: MediaPlayer? = null
    fun ring(context: Context) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            )
            isLooping = true
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun ring(context: Context, ms: Long) {
        ring(context)
        Handler(Looper.getMainLooper()).postDelayed({ stop() }, ms)
    }

}