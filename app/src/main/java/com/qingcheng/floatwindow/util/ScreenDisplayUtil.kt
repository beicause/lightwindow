package com.qingcheng.floatwindow.util

import android.content.Context
import android.content.res.Configuration

object ScreenDisplayUtil  {
        fun getWidth(context: Context):Int{
            return context.resources.displayMetrics.widthPixels
        }
        fun getHeight(context: Context):Int{
            return context.resources.displayMetrics.heightPixels
        }
        fun isLandscape(context: Context):Boolean{
            return context.resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE
    }
}