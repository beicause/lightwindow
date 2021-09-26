package com.qingcheng.lightwindow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
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
            requestOverlaysPermissionDialog(this)
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

    /**
     * 打开对话框申请悬浮窗权限
     * @param activity
     * */
    private fun requestOverlaysPermissionDialog(activity: ComponentActivity) {
        val alertDialogBuild = AlertDialog.Builder(activity)
        alertDialogBuild.setTitle("注意")
            .setMessage("运行本应用前，请进行如下配置：\n1.请授予悬浮窗权限。\n2.若开启省流量模式，请将本应用加入白名单或关闭省流量模式")
            .setPositiveButton("设置") { _, _ ->
                PermissionRequestUtil.requestOverlaysPermission(activity)
                activity.finish()
            }
            .setNegativeButton("退出") { _, _ -> activity.finish() }
            .setOnCancelListener { activity.finish() }
            .create().show()
    }
}
