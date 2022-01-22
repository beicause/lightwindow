package com.qingcheng.base

import android.content.Context
import android.webkit.JavascriptInterface
import com.qingcheng.base.util.FileUtil
import com.qingcheng.base.util.ToastUtil

class BaseJsInterface (val context: Context){
    @JavascriptInterface
    fun clearPageCache(){
        FileUtil.deleteDir(context.cacheDir)
        runOnUI { ToastUtil.showToast("成功，请重启") }
    }
}