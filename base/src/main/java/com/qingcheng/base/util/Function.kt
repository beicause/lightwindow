package com.qingcheng.base.util

import kotlinx.coroutines.*

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