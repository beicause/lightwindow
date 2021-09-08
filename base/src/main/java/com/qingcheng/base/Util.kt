package com.qingcheng.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun runOnUI(block:()->Unit){
    MainScope().launch { withContext(Dispatchers.Main){block()} }
}