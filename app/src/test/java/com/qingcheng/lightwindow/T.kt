package com.qingcheng.lightwindow

fun main() {
    var a = { print("a") }
    val b = a
    a={b();println("b")}
    a()
}

