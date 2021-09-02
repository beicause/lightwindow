package com.qingcheng.calender.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.qingcheng.calender.service.CoreService
/**
 * 开机开启服务，重设闹钟
 * */
class BootReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startForegroundService(Intent(context, CoreService::class.java))
    }
}