package com.qingcheng.floatwindow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qingcheng.floatwindow.service.ForegroundService
import com.qingcheng.floatwindow.service.MainAccessibilityService
import com.qingcheng.floatwindow.util.*
import com.qingcheng.floatwindow.util.CacheName.CACHE_IS_FIRST
import com.qingcheng.floatwindow.view.GuideView
import com.qingcheng.floatwindow.view.ViewManager

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesUtil.put(this, CACHE_IS_FIRST, true)
        if (ToastUtil.context == null) ToastUtil.context = this.applicationContext
        if (!PermissionRequestUtil.isOverlays(this) || !MainAccessibilityService.isEnable)
            PermissionRequestUtil.requestPermission(this)
        else {
            if (!ForegroundService.isEnable) startForegroundService(
                Intent(
                    this,
                    ForegroundService::class.java
                )
            )
            startActivity(Intent(this, SettingsActivity::class.java))
            if (SharedPreferencesUtil.getBoolean(this@MainActivity, CACHE_IS_FIRST))
                ViewManager.new(::GuideView, this).apply {
                    textViewUtil.setText(
                        "我会在适当的时候给出不同的内容",
                        "倒置手机即可打开悬浮窗",
                        "轻程logo往往不是摆设").printNextText()
                    addToWindow()
                }
            finish()
        }
    }
}
