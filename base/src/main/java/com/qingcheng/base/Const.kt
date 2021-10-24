package com.qingcheng.base

import kotlinx.coroutines.*

const val isTest = false

const val PACKAGE_NAME = "com.qingcheng.lightwindow"
const val ACTION_START_CALENDAR = "$PACKAGE_NAME.ACTION_START_CALENDAR"
const val ACTION_START_MAIN = "$PACKAGE_NAME.ACTION_START_MAIN"
const val uiWebViewServiceName = "$PACKAGE_NAME.UIWebViewService"
const val calendarNoticeService = "com.qingcheng.calendar.service.CalendarNoticeService"

val INDEX_URL = "https://${if (isTest) "www." else ""}qingcheng.asia"
val CALENDAR_URL = "$INDEX_URL/calendar"
val MAIN_URL = "$INDEX_URL/main"

const val JS_INTERFACE_NAME = "Android"

//缓存
const val SP_CACHE_NAME = "CACHE"
const val NOT_FIRST = "NOT_FIRST"
const val WEB_VERSION = "WEB_VERSION"
const val IGNORE_VERSION = "IGNORE_VERSION"
const val MAIN_WIDTH = "MAIN_WIDTH"
const val MAIN_HEIGHT = "MAIN_HEIGHT"
const val POLICY = "POLICY"
const val ENABLE_SENSOR = "ENABLE_SENSOR"

const val appKey = "614bfb7b16b6c75de06de250"
const val channel = "default"

fun runOnUI(block: suspend CoroutineScope.() -> Unit) {
    MainScope().launch { withContext(Dispatchers.Main) { this.block() } }
}