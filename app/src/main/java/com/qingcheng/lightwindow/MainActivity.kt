package com.qingcheng.lightwindow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.qingcheng.base.ACTION_START_MAIN
import com.qingcheng.base.util.PermissionRequestUtil
import com.qingcheng.base.util.ToastUtil
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.umeng.analytics.MobclickAgent


class MainActivity : AppCompatActivity() {
    private val requestCode = 0

    private fun start() {
        MobclickAgent.onResume(this)
        if (!PermissionRequestUtil.isOverlays(this)) {
            MobclickAgent.onPause(this)
            setTheme(R.style.Theme_LightWindow)
            PermissionRequestUtil.requestOverlaysPermissionDialog(this)
        } else if (!PermissionRequestUtil.isReadPhoneState(this)) {
            MobclickAgent.onPause(this)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                requestCode
            )
        } else {
            ToastUtil.context = this.applicationContext
            ToastUtil.offsetBottom()
            startService(Intent(this, WebViewService::class.java).apply {
                action = ACTION_START_MAIN
            })
            MobclickAgent.onPause(this)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode && grantResults.size == 1)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                start()
            else finish()
    }
}
