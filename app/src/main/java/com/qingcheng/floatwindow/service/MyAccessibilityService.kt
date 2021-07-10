package com.qingcheng.floatwindow.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.view.accessibility.AccessibilityEvent
import com.qingcheng.floatwindow.receiver.ScreenStateReceiver
import com.qingcheng.floatwindow.util.VibratorUtil
import com.qingcheng.floatwindow.view.CalendarWebView
import java.util.*

class MyAccessibilityService : AccessibilityService() {
    private lateinit var sensorListener: SensorListener
    private lateinit var sp: SharedPreferences
    private val v = { VibratorUtil.vibrate(this, 100) }

    companion object {
        var isEnable: Boolean = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onServiceConnected() {
        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sp.getBoolean("notice", true))
            startForegroundService(Intent(this, ForegroundService::class.java))
        start()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopService(Intent(this, ForegroundService::class.java))
        isEnable = false
        return super.onUnbind(intent)
    }

    private fun start() {
        val calendarView = CalendarWebView.getInstance(this)!!
        var time = 0L
        var f=0
        sensorListener = SensorListener(this).apply {
            onTrigger = {
                it?.let {
                    if (it.values[2] < -8) {
                        if (f==0 && sp.getBoolean(
                                "vibrate",
                                false
                            ) && !calendarView.isAddToWindow
                        ) {
                            VibratorUtil.vibrate(this@MyAccessibilityService, 100)
                            calendarView.rotateIn()
                            time = System.currentTimeMillis()
                            f=1
                        }
                        if (f==1&&System.currentTimeMillis() - time >= 1500) {
                            f=2
                            calendarView.rotateOut()
                        }
                    }
                    else if (it.values[2]>0)f=0
                }
            }
            enable(Sensor.TYPE_GRAVITY)
        }
        ScreenStateReceiver().apply {
            onScreenOn = {
                sensorListener.enable(Sensor.TYPE_GRAVITY)
            }
            onScreenOff = {
                sensorListener.disable()
                calendarView.removeFromWindow()
            }
            enable(this@MyAccessibilityService)
        }
        isEnable = true
    }
}