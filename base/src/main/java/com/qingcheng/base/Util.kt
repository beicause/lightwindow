package com.qingcheng.base

import android.app.Service
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.util.FileUtil
import com.qingcheng.base.util.NetworkRequestUtil
import com.qingcheng.base.util.SharedPreferencesUtil
import com.qingcheng.base.util.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

fun runOnUI(block: () -> Unit) {
    MainScope().launch { withContext(Dispatchers.Main) { block() } }
}

fun checkVersion(service: Service) {
    val localVersion =
        SharedPreferencesUtil.getString(service, CacheName.CACHE_VERSION.name)
    MainScope().launch {
        try {
            NetworkRequestUtil.getVersion().body?.string()
        } catch (e: Exception) {
            runOnUI { ToastUtil.showToast("网络异常") }
            null
        }?.let {
            val json = JSONObject(it)
            val remoteVersion = json.getString("version").toInt()
//            val isAppUpdate=json.getString("isAppUpdate")
            if (remoteVersion != (if (localVersion == "null") 0 else localVersion.toInt()))
                FileUtil.deleteDir(service.cacheDir)
        }
    }
}