//package com.qingcheng.floatwindow.service
//
//import android.app.*
//import android.content.Intent
//import android.hardware.Sensor
//import android.os.IBinder
//import com.qingcheng.floatwindow.MainActivity
//import com.qingcheng.floatwindow.R
//import com.qingcheng.floatwindow.receiver.ScreenStateReceiver
//import com.qingcheng.floatwindow.util.VibratorUtil
//
//class MainService : Service() {
//    private val noticeId = 1
//    private val channelId = "1"
//    private val channelName = "轻程主服务"
//    private lateinit var sensorListener: SensorListener
//    private lateinit var screenStateReceiver: ScreenStateReceiver
//
//    companion object{
//        var isEnable=false
//    }
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        startForeground()
//        startListener()
//        isEnable=true
//    }
//
//    override fun onDestroy() {
//        screenStateReceiver.disable(this)
//        sensorListener.disable()
//        isEnable=false
//    }
//
//    private fun startForeground(){
//        val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val channel =
//            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
//                importance = NotificationManager.IMPORTANCE_LOW
//            }
//        manager.createNotificationChannel(channel)
//        startForeground(
//            noticeId, Notification.Builder(this, channelId)
//                .setContentTitle("轻程正在运行")
//                .setContentText("点击进入设置页面")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setVisibility(Notification.VISIBILITY_SECRET)
//                .setContentIntent(
//                    PendingIntent.getActivity(
//                        this,
//                        0,
//                        Intent(this, MainActivity::class.java),
//                        0
//                    )
//                )
//                .build()
//        )
//    }
//
//    private fun startListener(){
//        var time = 0L
//        var f = 0
//        val sp=getSharedPreferences("settings", MODE_PRIVATE)
//        sensorListener = SensorListener(this).apply {
//            onTrigger = {
//                it?.let {
//                    if (it.values[2] < -8) {
//                        f++
//                        if (f == 10) {
//                            if (sp.getBoolean("vibrate", false))
//                                VibratorUtil.vibrate(this@MainService, 100)
//                            startService(Intent(this@MainService,FloatWindowService::class.java))
//                            time = System.currentTimeMillis()
//                        }
//                        if (f > 10 && System.currentTimeMillis() - time >= 800) {
//                            stopService(Intent(this@MainService,FloatWindowService::class.java))
//                        }
//                    } else if (it.values[2] > 0) f = 0
//                }
//            }
//            enable(Sensor.TYPE_GRAVITY)
//        }
//        screenStateReceiver = ScreenStateReceiver().apply {
//            onScreenOn = {
//                sensorListener.enable(Sensor.TYPE_GRAVITY)
//            }
//            onScreenOff = {
//                sensorListener.disable()
//                stopService(Intent(this@MainService,FloatWindowService::class.java))
//            }
//            enable(this@MainService)
//        }
//    }
//}