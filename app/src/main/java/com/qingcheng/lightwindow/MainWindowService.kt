package com.qingcheng.lightwindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.util.VersionUtil
import com.qingcheng.base.view.ViewManager
import com.qingcheng.lightwindow.view.MainView

/**
 * 创建主界面悬浮窗的服务，运行于进程 :main_window
 * */
class MainWindowService : Service() {
    lateinit var viewManager: ViewManager
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        ToastUtil.context = this
        ToastUtil.offsetBottom()
        viewManager = ViewManager(this)
        VersionUtil.checkVersionUpdate(this)

        viewManager.new<MainView>(MainView(this, viewManager)).apply {
            zoomIn()
        }
    }

    override fun onDestroy() {
        viewManager.destroy(MainView::class)
        Process.killProcess(Process.myPid())
    }
}