package com.qingcheng.lightwindow.ui

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import com.qingcheng.base.PACKAGE_NAME
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.view.BaseFloatWindow

class UIService:Service() {
    companion object{
        const val ACTION_SHOW_SNOW="$PACKAGE_NAME.ACTION_SHOW_SNOW"
        const val ACTION_CLOSE_SNOW="$PACKAGE_NAME.ACTION_CLOSE_SNOW"
        const val IS_SNOW_RUNNING="is_snow_running"

    }
    var snowflakeView:BaseFloatWindow<SnowflakeView>?=null

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_SHOW_SNOW->showSnow(this)
            ACTION_CLOSE_SNOW-> {
                closeSnow()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showSnow(context: Context) {
        snowflakeView = BaseFloatWindow(
            context,
            SnowflakeView(context)
        ).apply {
            applyParams {
                width = ScreenUtil.getWidth(context)
                height = ScreenUtil.getHeight(context)
                flags =
                    flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
            addToWindow()
        }
        PreferencesUtil.putString(this, IS_SNOW_RUNNING,"1")
    }
    private fun closeSnow(){
        snowflakeView?.removeFromWindow()
        snowflakeView =null
        PreferencesUtil.removeString(this, IS_SNOW_RUNNING)
    }

    override fun onDestroy() {
        closeSnow()
    }
}
