package com.qingcheng.base.util

import android.content.Context
import android.content.Intent
import android.os.Build
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.service.VersionUpdateService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object VersionUtil {
    fun checkVersionUpdate(context: Context) {
        val localWebVersion =
            SharedPreferencesUtil.getString(context, CacheName.WEB_VERSION.name)
        val localAppVersion =
            "" + context.packageManager.getPackageInfo(context.packageName, 0).let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode else it.versionCode
            }
        MainScope().launch {
            try {
                NetworkRequestUtil.getVersion().body?.string()
            } catch (e: Exception) {
                runOnUI { ToastUtil.showToast("网络异常") }
                null
            }?.let {
                val json = JSONObject(it)
                val webVersion = json.getString("web_version")
                val appVersion = json.getString("app_version")
                if (webVersion != localWebVersion)
                    FileUtil.deleteDir(context.cacheDir)
                if (appVersion != localAppVersion)
                    if (SharedPreferencesUtil.getString(
                            context,
                            CacheName.IGNORE_VERSION.name
                        ) != appVersion
                    )
                        context.startService(Intent(context, VersionUpdateService::class.java))
            }
        }
    }
}