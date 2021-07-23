package com.qingcheng.floatwindow.service

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorListener(context: Context) : SensorEventListener {
    var onTrigger: (event: SensorEvent?) -> Unit = {}
    var isEnable = false
        private set
    private val manager: SensorManager =
        context.getSystemService(Service.SENSOR_SERVICE) as SensorManager

    companion object {
        fun isAvailable(context: Context, type: Int): Boolean {
            val manager = context.getSystemService(Service.SENSOR_SERVICE) as SensorManager
            return manager.getDefaultSensor(type) != null
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        onTrigger(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun enable(type: Int) {
        manager.registerListener(
            this,
            manager.getDefaultSensor(type),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        isEnable = true
    }

    fun disable() {
        manager.unregisterListener(this)
        isEnable = false
    }
}