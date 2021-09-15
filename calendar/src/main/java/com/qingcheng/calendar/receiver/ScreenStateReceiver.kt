package com.qingcheng.calendar.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
/**
 * 屏幕锁屏监听的广播接收器
 * */
@SuppressLint("StaticFieldLeak")
object ScreenStateReceiver : BroadcastReceiver() {
    var isEnable=false
    private var context:Context?=null
    /**
     * 亮屏回调函数
     * */
    var onScreenOn:()->Unit= {}

    /**
     * 息屏回调函数
     * */
    var onScreenOff:()->Unit = {}

    fun init(context: Context): ScreenStateReceiver {
        ScreenStateReceiver.context =context
        return this
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action==Intent.ACTION_SCREEN_ON) onScreenOn()
        if (intent?.action==Intent.ACTION_SCREEN_OFF) onScreenOff()
    }
    /**
     * 开启监听
     * */
    fun enable(){
        context!!.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        })
        isEnable =true
    }
    /**
     * 关闭监听
     * */
    fun disable(){
        context!!.unregisterReceiver(this)
        isEnable =false
    }
}