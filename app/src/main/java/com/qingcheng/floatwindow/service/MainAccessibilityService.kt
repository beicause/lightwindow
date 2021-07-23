package com.qingcheng.floatwindow.service

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.Sensor
import android.view.accessibility.AccessibilityEvent
import com.qingcheng.floatwindow.receiver.ScreenStateReceiver
import com.qingcheng.floatwindow.util.ToastUtil
import com.qingcheng.floatwindow.util.VibratorUtil

class MainAccessibilityService : AccessibilityService() {

    private lateinit var sensorListener: SensorListener
    private lateinit var screenStateReceiver: ScreenStateReceiver

    companion object {
        var isEnable: Boolean = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        ToastUtil.context=this
        if (ForegroundService.isEnable)stopService(Intent(this,ForegroundService::class.java))
        startForegroundService(Intent(this, ForegroundService::class.java))
        startListener()
        isEnable=true
    }

    override fun onUnbind(intent: Intent?): Boolean {
        ToastUtil.context=null
        sensorListener.disable()
        screenStateReceiver.disable(this)
        stopService(Intent(this, ForegroundService::class.java))
        stopService(Intent(this,FloatWindowService::class.java))
        isEnable = false
        return super.onUnbind(intent)
    }
    private fun startListener(){
        var time = 0L
        var f = 0
        val sp=getSharedPreferences("settings", MODE_PRIVATE)
        val manager: KeyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        sensorListener = SensorListener(this).apply {
            onTrigger = {
                it?.let {
                    if (it.values[2] < -8) {
                        f++
                        if (f == 10 &&!manager.isKeyguardLocked) {
                            if (sp.getBoolean("vibrate", false))
                                VibratorUtil.vibrate(this@MainAccessibilityService, 100)
                            startService(Intent(this@MainAccessibilityService,FloatWindowService::class.java))
                            time = System.currentTimeMillis()
                        }
                        if (f > 10 && System.currentTimeMillis() - time >= 800) {
                            stopService(Intent(this@MainAccessibilityService,FloatWindowService::class.java))
                        }
                    } else if (it.values[2] > 0) f = 0
                }
            }
            enable(Sensor.TYPE_GRAVITY)
        }
        screenStateReceiver = ScreenStateReceiver().apply {
            onScreenOn = {
                sensorListener.enable(Sensor.TYPE_GRAVITY)
            }
            onScreenOff = {
                sensorListener.disable()
                stopService(Intent(this@MainAccessibilityService,FloatWindowService::class.java))
            }
            enable(this@MainAccessibilityService)
        }
    }
}