package com.qingcheng.floatwindow.util

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object NetworkRequest {
    inline fun hfutLogin(
        username: String,
        password: String,
        crossinline onFailure: () -> Unit,
        crossinline onResponse: (Response) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://smile.huii.top/user/login")
            .post(
                FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build()
            )
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

    fun cacheHfutSchedule(context: Context){

        val token = SharedPreferencesUtil.getString(context,CacheName.CACHE_HFUT_TOKEN)
        val url = "https://smile.huii.top/apps/schedule"
        OkHttpClient().newCall(
            Request.Builder()
                .url(url)
                .header("token", token?:"")
                .build()
        ).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                ToastUtil.show("网络异常")
            }

            override fun onResponse(call: Call, response: Response) {
               // SharedPreferencesUtil.put(context,CacheName.)
            }

        })
    }
    inline fun sendEmail(
        email:String,
        crossinline onFailure: () -> Unit,
        crossinline onResponse: (Response) -> Unit
    ){
        val key="5cf73f734d96ccb9d024994f293d8d817851508b70f9cc401aef5a331745b622"
        val request = Request.Builder()
            .url("https://qingcheng.asia/user/email")
            .post(
                "{\"email\":\"$email\",\"key\":\"$key\"}".toRequestBody("application/json;charset=utf-8".toMediaType())
            )
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

    inline fun login(
        email:String,
        code:String,
        crossinline onFailure: () -> Unit,
        crossinline onResponse: (Response) -> Unit
    ){
        val request = Request.Builder()
            .url("https://qingcheng.asia/user/login")
            .post(
                "{\"email\":\"$email\",\"code\":\"$code\"}".toRequestBody("application/json;charset=utf-8".toMediaType())
            )
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