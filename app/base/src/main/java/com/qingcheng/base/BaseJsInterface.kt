package com.qingcheng.base

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

open class BaseJsInterface(private val context: Context, private val webView: WebView) {
  @JavascriptInterface
  fun fetch(url: String, options: String) {
    val opt = if (options == "undefined") null else JSONObject(options)
    val h = opt?.getJSONObject("headers")
    val k = h?.keys()
    val headers = Headers.Builder()
    k?.forEach { headers.add(it, h.getString(it)) }
    val request = Request.Builder()
      .method(
        opt?.getString("method") ?: "GET",
        opt?.getJSONObject("body")?.toString()?.toRequestBody()
      )
      .headers(headers.build())
      .url(url)
      .build()
    OkHttpClient().newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        runOnUI {
          webView.evaluateJavascript(
            "javascript: window.${JS_INTERFACE_NAME}.onNetFailure(`${e.message}`)",
            null
          )
        }
      }

      override fun onResponse(call: Call, response: Response) {
        val res = response.body?.string()
        runOnUI {
          webView.evaluateJavascript(
            "javascript: window.${JS_INTERFACE_NAME}.onNetResponse(`${res}`)",
            null
          )
        }
      }
    })
  }

  @JavascriptInterface
  fun writeClipboard(str: String) {
    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    manager.setPrimaryClip(ClipData.newPlainText(str, str))
  }
}
