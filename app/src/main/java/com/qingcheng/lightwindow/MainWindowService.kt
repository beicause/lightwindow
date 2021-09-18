package com.qingcheng.lightwindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.qingcheng.base.checkVersion
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.ViewManager
import com.qingcheng.lightwindow.view.MainView

/**
 * 创建主界面悬浮窗的服务，运行于进程 :main_window
 * */
class MainWindowService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()

        checkVersion(this)

        ViewManager.new(::MainView, this).apply {
            stopService = { stopSelf() }
            zoomIn()
        }
    }

    override fun onDestroy() {
        ViewManager.destroy(MainView::class)
        Process.killProcess(Process.myPid())
    }
}