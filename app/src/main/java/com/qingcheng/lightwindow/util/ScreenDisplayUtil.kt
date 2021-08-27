package com.qingcheng.lightwindow.util

import android.content.Context
import android.content.res.Configuration
/**
 * 屏幕显示
 * */
object ScreenDisplayUtil {
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
}