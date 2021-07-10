package com.qingcheng.floatwindow.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ComponentActivity
import com.qingcheng.floatwindow.service.MyAccessibilityService

object PermissionUtil {
    fun isOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isAccessibility(): Boolean {
        return MyAccessibilityService.isEnable
    }

    fun isIgnoreBattery(context: Context): Boolean {
        return (context.getSystemService(Service.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
            context.packageName
        )
    }

    fun isRead(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestOverlaysPermission(activity: ComponentActivity) {
        val alertDialogBuild = AlertDialog.Builder(activity)
        alertDialogBuild.setTitle("提示")
            .setMessage("缺少悬浮窗权限。\n使用本应用必须开启悬浮窗权限，点击确定前往开启。")
            .setPositiveButton("确定") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = Uri.parse("package:" + activity.packageName)
                })
                activity.finish()
            }
            .setNegativeButton("取消") { _, _ -> activity.finish() }.create().show()

    }

    fun requestOverlaysPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse("package:" + context.packageName)
        })
    }

    fun requestAccessibilityPermission(activity: ComponentActivity) {
        val alertDialogBuild = AlertDialog.Builder(activity)
        alertDialogBuild.setTitle("提示")
            .setMessage("缺少无障碍权限。\n使用本应用必须开启无障碍权限，点击确定前往开启。")
            .setPositiveButton("确定") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                activity.finish()
            }
            .setNegativeButton("取消") { _, _ -> activity.finish() }.create().show()
    }

    fun requestAccessibilityPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    @SuppressLint("BatteryLife")
    fun requestIgnoreBatteryPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse("package:" + context.packageName)
        })
    }

    fun requestReadPermission(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse("package:" + context.packageName)
        })
    }

}
