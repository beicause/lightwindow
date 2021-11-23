package com.qingcheng.base

import android.content.Context
import android.webkit.JavascriptInterface
import com.qingcheng.base.util.FileUtil

class BaseJsInterface (val context: Context){
    @JavascriptInterface
    fun clearPageCache(){
        FileUtil.deleteDir(context.cacheDir)
    }
}