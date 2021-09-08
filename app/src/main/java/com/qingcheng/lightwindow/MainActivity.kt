package com.qingcheng.lightwindow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qingcheng.base.util.PermissionRequestUtil
import com.qingcheng.base.util.ToastUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionRequestUtil.isOverlays(this))
            PermissionRequestUtil.requestOverlaysPermissionDialog(this)
        else {
            ToastUtil.context = this
            ToastUtil.offsetBottom()
            startService(Intent(this, MainWindowService::class.java))
            finish()
        }
    }
}

