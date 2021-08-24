package com.qingcheng.lightwindow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.qingcheng.lightwindow.service.CoreService

class BootReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startForegroundService(Intent(context, CoreService::class.java))
    }
}