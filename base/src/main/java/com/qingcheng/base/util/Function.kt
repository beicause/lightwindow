package com.qingcheng.base.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun runOnUI(block: () -> Unit) {
    MainScope().launch { withContext(Dispatchers.Main) { block() } }
}

//fun killAllProcess(context: Context){
//    val manager=context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
//    manager.runningAppProcesses.forEach {
//        if (it.pid!=Process.myPid())Process.killProcess(it.pid)
//    }
//    Process.killProcess(Process.myPid())
//}