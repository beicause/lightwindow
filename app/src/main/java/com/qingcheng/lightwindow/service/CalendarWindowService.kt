package com.qingcheng.lightwindow.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import androidx.room.Room
import com.qingcheng.lightwindow.database.EventDataBase
import com.qingcheng.lightwindow.util.FileUtil
import com.qingcheng.lightwindow.util.NetworkRequestUtil
import com.qingcheng.lightwindow.util.ScreenDisplayUtil
import com.qingcheng.lightwindow.util.SharedPreferencesUtil
import com.qingcheng.lightwindow.view.*
import com.qingcheng.lightwindow.cache.CacheName
import org.json.JSONObject

/**
 * 日程表悬浮窗服务
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
        dataBase= Room.databaseBuilder(applicationContext, EventDataBase::class.java,"events")
            .enableMultiInstanceInvalidation().build()
        val localVersion =
            SharedPreferencesUtil.getString(this, CacheName.CACHE_CLD_VERSION.keyName)
        NetworkRequestUtil.getVersion({}, { response ->
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
                if (ScreenDisplayUtil.isLandscape(this@CalendarWindowService)) {
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