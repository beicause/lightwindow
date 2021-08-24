package com.qingcheng.lightwindow

fun main() {
  val alarm="*20"
  val a=0 - (if (alarm.substring(
      0,
      1
    ) == "*"
  ) alarm.substring(1) else alarm).toLong() * 60
  print(a)
}

