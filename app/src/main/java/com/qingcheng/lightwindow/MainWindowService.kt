package com.qingcheng.lightwindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.runOnUI
import com.qingcheng.base.util.FileUtil
import com.qingcheng.base.util.NetworkRequestUtil
import com.qingcheng.base.util.SharedPreferencesUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.ViewManager
import org.json.JSONObject

/**
 * 创建主界面悬浮窗的服务，运行于进程 :main_window
 * */
class MainWindowService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        ToastUtil.context=this
        ToastUtil.offsetBottom()

        val version =
            SharedPreferencesUtil.getString(this, CacheName.CACHE_MAIN_VERSION.name)
        NetworkRequestUtil.getVersion({ runOnUI{ ToastUtil.showToast("网络异常") } }, { response ->
            response.body?.string()?.let {
                Log.i("获取版本",it)
                if (JSONObject(it).getString("version")
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