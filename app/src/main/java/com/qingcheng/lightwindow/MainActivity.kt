package com.qingcheng.lightwindow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qingcheng.base.util.ACTION_START_MAIN
import com.qingcheng.base.util.PermissionRequestUtil
import com.qingcheng.base.util.ToastUtil
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        if (!PermissionRequestUtil.isOverlays(this))
            PermissionRequestUtil.requestOverlaysPermissionDialog(this)
        else {
            ToastUtil.context = this
            ToastUtil.offsetBottom()
            startService(Intent(this, WebViewService::class.java).apply {
                action = ACTION_START_MAIN
            })
            finish()
        }
    }
}
