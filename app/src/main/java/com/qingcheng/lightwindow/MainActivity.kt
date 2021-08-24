package com.qingcheng.lightwindow

import android.content.Intent
import android.hardware.Sensor
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qingcheng.lightwindow.receiver.SensorListener
import com.qingcheng.lightwindow.service.CoreService
import com.qingcheng.lightwindow.service.MainWindowService
import com.qingcheng.lightwindow.util.PermissionRequestUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionRequestUtil.isOverlays(this) /*|| !MainAccessibilityService.isEnable*/)
            PermissionRequestUtil.requestOverlaysPermissionDialog(this)
        else {
            if (!SensorListener.isAvailable(this, Sensor.TYPE_GRAVITY)) {
                Toast.makeText(this, "您的手机不支持重力传感器，无法运行日程表", Toast.LENGTH_SHORT).show()
            } else startForegroundService(Intent(this, CoreService::class.java))
            startService(Intent(this, MainWindowService::class.java))
            finish()
        }
    }
}

