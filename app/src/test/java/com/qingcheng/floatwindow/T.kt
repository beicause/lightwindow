package com.qingcheng.floatwindow

fun main() {
    val s= mutableSetOf<()->Unit>()
    var a={}
    s.add(a)
    s.add(a)
    print(s.size)
}