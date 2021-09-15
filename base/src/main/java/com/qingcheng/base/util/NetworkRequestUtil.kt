package com.qingcheng.base.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * 网络操作
 * */
object NetworkRequestUtil {
    /**
     * 获取服务器提供的网页版本号，用于和本地网页版本号进行比较，实现更新
     * */
    suspend fun getVersion(): Response {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://874be9c6-69bb-473d-9ba3-8afc02442e35.bspapp.com/http/version")
                .build()
            OkHttpClient().newCall(request).execute()
        }
    }
}