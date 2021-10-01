package com.qingcheng.base

import kotlinx.coroutines.*

const val packageName = "com.qingcheng.lightwindow"
const val ACTION_START_CALENDAR = "$packageName.ACTION_START_CALENDAR"
const val ACTION_START_MAIN = "$packageName.ACTION_START_MAIN"
const val uiWebViewServiceName = "$packageName.UIWebViewService"
const val CALENDAR_URL = "https://qingcheng.asia/calendar"
const val MAIN_URL = "https://qingcheng.asia/main"
const val JS_INTERFACE_NAME = "Android"
const val SP_CACHE_NAME = "CACHE"
const val WEB_VERSION = "WEB_VERSION"
const val IGNORE_VERSION = "IGNORE_VERSION"
const val MAIN_WIDTH = "MAIN_WIDTH"
const val MAIN_HEIGHT = "MAIN_HEIGHT"
const val POLICY = "POLICY"
const val appKey = "614bfb7b16b6c75de06de250"
const val channel = "default"
fun runOnUI(block: suspend CoroutineScope.() -> Unit) {
    MainScope().launch { withContext(Dispatchers.Main) { this.block() } }
}