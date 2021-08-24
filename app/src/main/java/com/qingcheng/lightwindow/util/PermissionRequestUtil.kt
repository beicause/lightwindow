package com.qingcheng.lightwindow.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ComponentActivity

object PermissionRequestUtil {
    /**
     * 判断悬浮窗权限
     * @param context
     * @return 是否可绘制悬浮窗
     * */
    fun isOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

//    fun isIgnoreBattery(context: Context): Boolean {
//        return (context.getSystemService(Service.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
//            context.packageName
//        )
//    }

    /**
     * 打开对话框申请悬浮窗权限
     * @param activity
     * */
    fun requestOverlaysPermissionDialog(activity: ComponentActivity) {
        val alertDialogBuild = AlertDialog.Builder(activity)
        alertDialogBuild.setTitle("提示")
            .setMessage("缺少悬浮窗权限。\n使用本应用必须开启悬浮窗权限，点击确定前往开启。")
            .setPositiveButton("确定") { _, _ ->
                requestOverlaysPermission(activity)
                activity.finish()
            }
            .setNegativeButton("取消") { _, _ -> activity.finish() }
            .setOnCancelListener { activity.finish() }
            .create().show()
    }

    fun requestOverlaysPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse("package:" + context.packageName)
        })
    }

//    @SuppressLint("BatteryLife")
//    fun requestIgnoreBatteryPermission(context: Context) {
//        context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            data = Uri.parse("package:" + context.packageName)
//        })
//    }
}
