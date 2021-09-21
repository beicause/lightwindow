package com.qingcheng.base.util

import kotlinx.coroutines.*

const val packageName = "com.qingcheng.lightwindow"
const val ACTION_START_CALENDAR = "$packageName.ACTION_START_CALENDAR"
const val ACTION_START_MAIN = "$packageName.ACTION_START_MAIN"
const val webViewServiceName = "$packageName.WebViewService"
const val CALENDAR_URL = "https://qingcheng.asia/calendar/"
const val MAIN_URL = "https://qingcheng.asia/main"
const val JS_INTERFACE_NAME = "Android"
fun runOnUI(block: suspend CoroutineScope.() -> Unit) {
    MainScope().launch { withContext(Dispatchers.Main) { this.block() } }
}
//fun killAllProcess(context: Context){
//    val manager=context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
//    manager.runningAppProcesses.forEach {
//        if (it.pid!=Process.myPid())Process.killProcess(it.pid)
//    }
//    Process.killProcess(Process.myPid())
//}