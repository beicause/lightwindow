package com.qingcheng.calendar.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import androidx.room.Room
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.runOnUI
import com.qingcheng.base.util.*
import com.qingcheng.base.view.ViewManager
import com.qingcheng.calendar.database.EventDataBase
import com.qingcheng.calendar.view.*
import org.json.JSONObject

/**
 * 创建日程表悬浮窗的服务，运行于进程 :cld_window
 * */
class CalendarWindowService : Service() {
    companion object{
        //声明为静态，为了在webview的js接口中调用
        var dataBase:EventDataBase?=null
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        ToastUtil.context=this
        ToastUtil.offsetBottom()

        dataBase= Room.databaseBuilder(applicationContext, EventDataBase::class.java,"events")
            .enableMultiInstanceInvalidation().build()
        val localVersion =
            SharedPreferencesUtil.getString(this, CacheName.CACHE_CLD_VERSION.name)
        NetworkRequestUtil.getVersion({ runOnUI { ToastUtil.showToast("网络异常") } }, { response ->
            response.body?.let {
                if (JSONObject(it.string()).getString("version")
                        .toInt() != (if (localVersion == "null") 0 else localVersion.toInt())
                ) {
                    FileUtil.deleteDir(this.cacheDir)
                }
            }
        })
        ViewManager.new(::ZoomView,this)
        ViewManager.new(::CalendarView, this@CalendarWindowService).apply {
            applyParams {
                if (ScreenUtil.isLandscape(this@CalendarWindowService)) {
                    val t = width
                    width = height
                    height = t
                }
            }
            rotateIn()
            stopService = { stopSelf() }
        }
    }

    override fun onDestroy() {
        dataBase?.close()
        ViewManager.destroy(ZoomView::class,CalendarView::class)
        Process.killProcess(Process.myPid())
    }
}