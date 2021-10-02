//package com.qingcheng.calendar.receiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.qingcheng.calendar.service.CalendarNoticeService
//
///**
// * 开机开启服务，重设闹钟
// * */
//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        if (intent?.action == Intent.ACTION_BOOT_COMPLETED)
//            context?.startForegroundService(
//                Intent(
//                    context,
//                    CalendarNoticeService::class.java
//                )
//            )
//    }
//}