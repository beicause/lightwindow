package com.qingcheng.floatwindow.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.qingcheng.floatwindow.util.ScreenDisplayUtil
import com.qingcheng.floatwindow.view.*

class FloatWindowService : Service() {

    companion object {
        var isEnable = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ViewManager.new(::GuideView,this)
        ViewManager.new(::BookView,this)
        ViewManager.new(::MusicView,this)
        ViewManager.new(::ZoomView,this)
        var isChange = false
        ViewManager.new(::CalendarView, this@FloatWindowService).apply {
            applyParams {
                if (ScreenDisplayUtil.isLandscape(this@FloatWindowService)) {
                    val t = width
                    width = height
                    height = t
                    isChange = true
                } else if (isChange) {
                    val t = width
                    width = height
                    height = t
                    isChange = false
                }
            }
            rotateIn()
            rotateOutListener = { stopSelf() }
        }
        isEnable = true
    }

    override fun onDestroy() {
        isEnable = false
        ViewManager.destroyAll()
        Process.killProcess(Process.myPid())
    }
}