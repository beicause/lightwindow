package com.qingcheng.base.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * 请求权限
 * */
object PermissionRequestUtil {
    /**
     * 判断悬浮窗权限
     * @param context
     * @return 是否可绘制悬浮窗
     * */
    fun isOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isReadPhoneState(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context, Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

//    fun isIgnoreBattery(context: Context): Boolean {
//        return (context.getSystemService(Service.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
//            context.packageName
//        )
//    }

    fun requestOverlaysPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse("package:" + context.packageName)
        })
    }


//    fun requestUsage(context: Context){
//        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
//            flags=Intent.FLAG_ACTIVITY_NEW_TASK
//        })
//    }
//    @SuppressLint("BatteryLife")
//    fun requestIgnoreBatteryPermission(context: Context) {
//        context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            data = Uri.parse("package:" + context.packageName)
//        })
//    }
}
