package com.qingcheng.lightwindow

import android.app.Application
import com.qingcheng.base.POLICY
import com.qingcheng.base.appKey
import com.qingcheng.base.channel
import com.qingcheng.base.util.SharedPreferencesUtil
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化SDK
        UMConfigure.preInit(this, appKey, channel)
        UMConfigure.setLogEnabled(true)
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true)
        MobclickAgent.setSessionContinueMillis(1000)

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)

        if (SharedPreferencesUtil.getString(this, POLICY) != "null")
            UMConfigure.init(this, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, "")
    }
}