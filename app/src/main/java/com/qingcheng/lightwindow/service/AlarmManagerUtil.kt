package com.qingcheng.lightwindow.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import com.qingcheng.lightwindow.database.getTimeString
import com.qingcheng.lightwindow.service.CoreService.Companion.NOTICE_ACTION
import java.util.*
import kotlin.collections.ArrayList

/**
 * 设置闹钟的工具类
 * */
object AlarmManagerUtil {
    /**
     * 设置闹钟，以时间秒数为requestCode，发送Intent至CoreService
     * @see CoreService.NOTICE_ACTION
     * @param context
     * @param triggerTime 闹钟触发的时间戳
     * */
    fun set(context: Context, triggerTime: Long) {
        val manager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            Intent(context, CoreService::class.java).apply {
                action = NOTICE_ACTION
            }.let { i ->
                PendingIntent.getService(
                    context,
                    (triggerTime/1000).toInt(),
                    i,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            })
        Log.i(
            "设置闹钟",
            triggerTime .getTimeString("yyyy-MM-dd HH:mm:ss")
        )
    }

    /**
     * 取消闹钟
     * @param context
     * @param triggerTime 闹钟触发的时间戳
     * */
    fun cancel(context: Context, triggerTime: Long) {
        val pi= Intent(context, CoreService::class.java).apply {
            action = NOTICE_ACTION
        }.let { i ->
            PendingIntent.getService(
                context, (triggerTime/1000).toInt(), i, PendingIntent.FLAG_NO_CREATE
            )
        } ?: return//这里空判断一下，若取消的闹钟不存在，会导致空指针
        val manager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        manager.cancel(pi)
        Log.i(
            "取消闹钟",
            triggerTime .getTimeString("yyyy-MM-dd HH:mm:ss")
        )
    }

    /**
     * 设置每天重复一次的闹钟，requestCode为0
     * @param context
     * @param triggerTime 首次触发的时间戳
     * */
    fun repeatDay(context: Context, triggerTime: Long) {
        val manager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        manager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            Intent(context, CoreService::class.java).apply {
                action = NOTICE_ACTION
            }.let { i ->
                PendingIntent.getService(
                    context,
                    0,
                    i,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            })
        Log.i(
            "设置每天重复闹钟",
            triggerTime .getTimeString("yyyy-MM-dd HH:mm:ss")
        )
    }

    /**
     * 设置系统闹钟
     *
     * @param context
     * @param message 闹钟名
     * @param time 闹钟时间，格式HH:mm开头即可
     * @param days 重复的星期数
     * @see Calendar.DAY_OF_WEEK
     * */
    fun setAlarmClock(
        context: Context,
        message: String,
        time: String,
        days: ArrayList<Int> = ArrayList(),
    ) {
        val t = time.split(":")
        context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).apply {
            Log.i("设置系统闹钟", "$message $time $days")
            putExtra(AlarmClock.EXTRA_MESSAGE, "$message(set by qingcheng)")
            putExtra(AlarmClock.EXTRA_HOUR, t[0].toInt())
            putExtra(AlarmClock.EXTRA_MINUTES, t[1].substring(0, 2).toInt())
            if (days.size != 0) putExtra(AlarmClock.EXTRA_DAYS, days)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}