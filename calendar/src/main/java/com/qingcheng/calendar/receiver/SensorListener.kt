package com.qingcheng.calendar.receiver

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * 传感器监听器
 * */
object SensorListener : SensorEventListener {
    /**
     * 传感器运行的处理方法
     * */
    var onTrigger: (event: SensorEvent?) -> Unit = {}
    var isEnable=false
    private var manager: SensorManager? = null

    fun init(context: Context) : SensorListener {
        manager = context.getSystemService(Service.SENSOR_SERVICE) as SensorManager
        return this
    }

    /**
     * 判断传感器是否可用
     * @param type 传感器类型
     * @see Sensor
     * */
    fun isAvailable(type: Int = Sensor.TYPE_GRAVITY): Boolean {
        return manager!!.getDefaultSensor(type) != null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        onTrigger(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * 开启传感器，默认重力传感器
     * @param type 传感器类型
     * @see Sensor
     * */
    fun enable(type: Int = Sensor.TYPE_GRAVITY) {
        manager!!.registerListener(
            this,
            manager!!.getDefaultSensor(type),
            SensorManager.SENSOR_DELAY_NORMAL, 200000
        )
        isEnable =true
    }

    /**
     * 关闭传感器
     * */
    fun disable() {
        manager!!.unregisterListener(this)
        isEnable =false
    }
}