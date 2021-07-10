package com.qingcheng.floatwindow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver:BroadcastReceiver() {
    companion object{
        const val ALARM_TRIGGER="com.qingcheng.floatwindow.ALARM_TRIGGER"
    }
    override fun onReceive(context: Context?, intent: Intent?) {

    }
}