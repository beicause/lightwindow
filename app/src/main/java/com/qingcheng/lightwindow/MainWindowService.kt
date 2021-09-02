package com.qingcheng.lightwindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.qingcheng.baseutil.cache.CacheName
import com.qingcheng.baseutil.util.FileUtil
import com.qingcheng.baseutil.util.NetworkRequestUtil
import com.qingcheng.baseutil.util.SharedPreferencesUtil
import com.qingcheng.baseutil.view.ViewManager
import org.json.JSONObject

/**
 * 创建主界面悬浮窗的服务
 * */
class MainWindowService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        val version =
            SharedPreferencesUtil.getString(this, CacheName.CACHE_MAIN_VERSION.name)
        NetworkRequestUtil.getVersion({}, { response ->
            response.body?.let {
                if (JSONObject(it.string()).getString("version")
                        .toInt() != (if (version == "null") 0 else version.toInt())
                ) {
                    FileUtil.deleteDir(this.cacheDir)
                }
            }
        })
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