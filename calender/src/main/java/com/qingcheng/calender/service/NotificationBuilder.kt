package com.qingcheng.calender.service

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
/**
 * 对NotificationCompat.Builder的包装，可以获取已设置的通知内容
 * @see NotificationCompat.Builder
 * */
class NotificationBuilder(context: Context, channelId: String) {
    var contentTitle: String? = null
        set(value) {
            field = value
            builder.setContentTitle(field)
        }
    var contentText: String? = null
        set(value) {
            field = value
            builder.setContentText(field)
        }
    var bigText: String? = null
        set(value) {
            field = value
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(field))
        }
    var subText: String? = null
        set(value) {
            field = value
            builder.setSubText(field)
        }
    val builder = NotificationCompat.Builder(context, channelId)
    fun build(): Notification = builder.build()
    override fun toString(): String {
        return "NotificationBuilder(contentTitle=$contentTitle, contentText=$contentText, bigText=$bigText, subText=$subText)"
    }
}