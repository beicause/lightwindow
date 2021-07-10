package com.qingcheng.floatwindow.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import com.qingcheng.floatwindow.MainActivity
import com.qingcheng.floatwindow.R

class ForegroundService : Service() {
    private val noticeId = 1
    private val channelId = "1"
    private val channelName = "轻程通知服务"

    companion object {
        var isEnable: Boolean = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                importance = NotificationManager.IMPORTANCE_LOW
            }
        manager.createNotificationChannel(channel)
        startForeground(
            noticeId, Notification.Builder(this, channelId)
                .setContentTitle("轻程正在运行")
                .setContentText("点击进入设置页面")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainActivity::class.java),
                        0
                    )
                )
                .build()
        )

        isEnable = true
        return START_STICKY
    }

    override fun onDestroy() {
        isEnable = false
    }
}