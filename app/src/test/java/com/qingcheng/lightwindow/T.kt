package com.qingcheng.lightwindow

import com.qingcheng.calendar.util.gnnu.GnnuRequest
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        println(
            GnnuRequest.getGnnuSchedule("", "")
        )
    }
//    var a = { print("a") }
//    val b = a
//    a={b();println("b")}
//    a()
}

