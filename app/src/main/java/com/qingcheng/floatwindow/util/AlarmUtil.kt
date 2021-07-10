package com.qingcheng.floatwindow.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import com.qingcheng.floatwindow.receiver.AlarmReceiver

object AlarmUtil   {

        private var manager: AlarmManager? = null

        @SuppressLint("UnspecifiedImmutableFlag")
        fun set(context: Context, triggerAtMillis: Long, id: Int,block:Intent.()->Unit) {
            manager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
            manager?.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                Intent(context, AlarmReceiver::class.java).apply {
                    action = AlarmReceiver.ALARM_TRIGGER
                    this.block()
                }.let { i -> PendingIntent.getBroadcast(context, id, i, 0) })
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun repeat(context: Context, triggerAtMillis: Long, intervalMillis: Long, id: Int, block:Intent.()->Unit) {
            manager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
            manager?.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis,
                Intent(context, AlarmReceiver::class.java).apply {
                    action = AlarmReceiver.ALARM_TRIGGER
                    this.block()
                }.let { i -> PendingIntent.getBroadcast(context, id, i, 0) })
        }

}