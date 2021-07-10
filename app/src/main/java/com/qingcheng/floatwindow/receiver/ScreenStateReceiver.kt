package com.qingcheng.floatwindow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenStateReceiver : BroadcastReceiver() {
    var onScreenOn:()->Unit= {}
    var onScreenOff:()->Unit = {}
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action==Intent.ACTION_SCREEN_ON)onScreenOn()
        if (intent?.action==Intent.ACTION_SCREEN_OFF)onScreenOff()
    }
    fun enable(context: Context){
        context.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        })
    }
    fun disable(context: Context){
        context.unregisterReceiver(this)
    }
}