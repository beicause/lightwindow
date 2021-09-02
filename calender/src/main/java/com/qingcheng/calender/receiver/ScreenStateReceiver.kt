package com.qingcheng.calender.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
/**
 * 屏幕锁屏监听的广播接收器
 * */
class ScreenStateReceiver : BroadcastReceiver() {
    /**
     * 亮屏回调函数
     * */
    var onScreenOn:()->Unit= {}

    /**
     * 息屏回调函数
     * */
    var onScreenOff:()->Unit = {}

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action==Intent.ACTION_SCREEN_ON)onScreenOn()
        if (intent?.action==Intent.ACTION_SCREEN_OFF)onScreenOff()
    }
    /**
     * 开启监听
     * */
    fun enable(context: Context){
        context.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        })
    }
    /**
     * 关闭监听
     * */
    fun disable(context: Context){
        context.unregisterReceiver(this)
    }
}