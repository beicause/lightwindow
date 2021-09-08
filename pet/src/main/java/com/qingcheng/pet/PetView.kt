package com.qingcheng.pet

import android.content.Context
import android.view.WindowManager
import android.widget.VideoView
import com.qingcheng.base.view.BaseFloatWindow

class PetView(context: Context) : BaseFloatWindow<VideoView>(context, VideoView(context)) {
    init {
        applyParams {
            width = 500.toIntDip()
            height = width
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        applyView {
//            setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.video}"))
//            start()
        }
    }
}