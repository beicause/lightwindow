package com.qingcheng.base.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.qingcheng.base.IGNORE_VERSION
import com.qingcheng.base.NOT_FIRST
import com.qingcheng.base.WEB_VERSION
import com.qingcheng.base.runOnUI
import com.qingcheng.base.service.VersionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object VersionUtil {
    suspend fun checkVersion(context: Context): String {
        var isAppUpdate = false
        var isWebUpdate = false
        val localWebVersion =
            PreferencesUtil.getString(context, WEB_VERSION)
        val localAppVersion =
            context.packageManager.getPackageInfo(context.packageName, 0).let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode else it.versionCode
            }.toString()

        var json: JSONObject? = null
        try {
            val res = withContext(Dispatchers.IO){NetworkRequestUtil.getVersion().body?.string()}
            json = if (res != null) JSONObject(res) else null
            json ?: throw NullPointerException("version json is null")
        } catch (e: NullPointerException) {
            runOnUI { ToastUtil.showToast("检查网络并解除省流量限制") }
            null
        }?.let {
            val webVersion = it.getString("web_version")
            val appVersion = it.getString("app_version")
            if (webVersion != localWebVersion)
                isWebUpdate = true
            if (appVersion != localAppVersion)
                isAppUpdate = true
        }
        return json?.apply {
            put("is_app_update", isAppUpdate)
            put("is_web_update", isWebUpdate)
            put("local_web_version", localWebVersion)
            put("local_app_version", localAppVersion)
        }.toString().also { Log.i("检查更新", it) }
    }

    suspend fun checkAndShowUpdate(context: Context) {
        val json = checkVersion(context)
        if (json == "null") return
        val versions = JSONObject(json)
        if (versions.getBoolean("is_app_update"))
            if (PreferencesUtil.getString(
                    context,
                    IGNORE_VERSION
                ) != versions.getString("app_version")
            ) {
                context.stopService(Intent(context, VersionService::class.java))
                context.startService(Intent(context, VersionService::class.java))
            }
        if (versions.getBoolean("is_web_update")) {
            FileUtil.deleteDir(context.cacheDir)
            PreferencesUtil.putString(context, NOT_FIRST, false.toString())
        }
    }
}