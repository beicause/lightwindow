package com.qingcheng.lightwindow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qingcheng.baseutil.util.PermissionRequestUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionRequestUtil.isOverlays(this))
            PermissionRequestUtil.requestOverlaysPermissionDialog(this)
        else {
//            startForegroundService(Intent(this, CoreService::class.java))
            startService(Intent(this, MainWindowService::class.java))
            finish()
        }
    }
}

