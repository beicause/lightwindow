package com.qingcheng.lightwindow.util

import okhttp3.*
import java.io.IOException

object NetworkRequestUtil {
    inline fun getVersion(
        crossinline onFailure: () -> Unit,
        crossinline onResponse: (Response) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://874be9c6-69bb-473d-9ba3-8afc02442e35.bspapp.com/http/version")
            .build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(response)
            }

        })
    }
}