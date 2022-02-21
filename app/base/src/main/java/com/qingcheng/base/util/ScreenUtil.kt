package com.qingcheng.base.util

import android.content.Context
import android.content.res.Configuration


/**
 * 屏幕显示
 * */
object ScreenUtil {
    /**
     *@return 屏幕宽度
     * */
    fun getWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * @return 屏幕高度
     * */
    fun getHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * @return 是否横屏
     * */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

//    fun topPackageName(context: Context): String? {
//        val manager = context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager
//        val name = manager
//            .queryUsageStats(
//                UsageStatsManager.INTERVAL_BEST,
//                System.currentTimeMillis()-60000 ,
//                System.currentTimeMillis()
//            )
//            .sortedByDescending { it.lastTimeUsed }.map { it.packageName }
//        ActivityManager
//        return name[0]
//    }
}