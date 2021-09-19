package com.qingcheng.calendar.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import androidx.room.Room
import com.qingcheng.base.util.*
import com.qingcheng.base.util.VersionUtil.checkVersionUpdate
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.database.EventDataBase
import com.qingcheng.calendar.view.*

/**
 * 创建日程表悬浮窗的服务，运行于进程 :cld_window
 * */
class CalendarWindowService : Service() {
    companion object {
        //声明为静态，为了在webview的js接口中调用
        var dataBase: EventDataBase? = null
    }

    private lateinit var viewManager: ViewManager
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()
        viewManager = ViewManager(this)
        dataBase = Room.databaseBuilder(applicationContext, EventDataBase::class.java, "events")
            .enableMultiInstanceInvalidation().build()

        checkVersionUpdate(this)

        viewManager.new<CalendarView>(CalendarView(this)).apply {
            applyParams {
                if (ScreenUtil.isLandscape(this@CalendarWindowService)) {
                    val t = width
                    width = height
                    height = t
                }
            }
            rotateIn()
        }
    }

    override fun onDestroy() {
        dataBase?.close()
        viewManager.destroy(CalendarView::class)
        Process.killProcess(Process.myPid())
    }
}