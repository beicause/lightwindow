package com.qingcheng.pet

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.qingcheng.base.view.ViewManager

class PetCoreService:Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        ViewManager.new(::PetView,this).addToWindow()
    }
}