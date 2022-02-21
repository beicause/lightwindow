package com.qingcheng.calendar.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.qingcheng.base.*
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.PreferencesUtil.not
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.util.VibratorUtil
import com.qingcheng.calendar.R
import com.qingcheng.calendar.database.Event
import com.qingcheng.calendar.database.EventDataBase
import com.qingcheng.calendar.database.getTime
import com.qingcheng.calendar.database.getTimeString
import com.qingcheng.calendar.receiver.ScreenStateReceiver
import com.qingcheng.calendar.receiver.SensorListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * 日程表核心服务，运行锁屏，传感器监听，设置并接收闹钟提醒，发送通知
 * */
class CalendarNoticeService : Service() {
    companion object {
        const val NOTICE_ACTION = "$PACKAGE_NAME.NOTICE_ACTION"
        const val SENSOR_CHANGE_ACTION = "$PACKAGE_NAME.SENSOR_CHANGE_ACTION"
    }

    private val scope = MainScope()
    private val mainNoticeId = 1
    private val mainChannelId = "1"
    private val mainChannelName = "事件通知"
    private lateinit var mainNotificationBuilder: NotificationBuilder

    private lateinit var dataBase: EventDataBase

    private var lastNoticeEvent: Event? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        initNotice()
        startForeground(mainNoticeId, mainNotificationBuilder.build())
        setAlarmAndMainNotice()
        if (!!PreferencesUtil.getString(this@CalendarNoticeService, ENABLE_SENSOR)) {
            ScreenStateReceiver.init(this)
            SensorListener.init(this)
            if (!SensorListener.isAvailable()) {
                ToastUtil.showToast("您的手机不支持重力传感器")
                stopSelf()
            } else startListener()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(this::class.qualifiedName, intent?.action ?: "null")
        when (intent?.action) {
            SENSOR_CHANGE_ACTION -> {
                Log.i(
                    "传感器",
                    intent.getBooleanExtra("sensor", false).toString()
                )
                if (intent.getBooleanExtra("sensor", false)) {
                    ScreenStateReceiver.init(this)
                    SensorListener.init(this)
                    if (!SensorListener.isAvailable()) {
                        ToastUtil.showToast("您的手机不支持重力传感器")
                        stopSelf()
                    } else startListener()
                } else {
                    if (SensorListener.isEnable)
                        SensorListener.disable()
                    if (ScreenStateReceiver.isEnable)
                        ScreenStateReceiver.disable()
                }
            }
            NOTICE_ACTION -> {
                Log.i("提醒", lastNoticeEvent.toString())
                NotificationManagerCompat.from(this)
                    .notify(mainNoticeId, mainNotificationBuilder.build())
                scope.launch {
                    delay(2000)
                    withContext(Dispatchers.IO) {
                        dataBase.eventDao().getEvents().first()
                            .let {
                                val nextNoticeEvent = updateNoticeBuilder(it)
                                updateAlarm(nextNoticeEvent)
                                Log.i("提醒完毕，更新下次事件", nextNoticeEvent.toString())
                                NotificationManagerCompat.from(this@CalendarNoticeService).notify(
                                    mainNoticeId,
                                    mainNotificationBuilder.build()
                                )
                            }
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        ToastUtil.context = null
        dataBase.close()
        stopForeground(true)
        if (SensorListener.isEnable)
            SensorListener.disable()
        if (ScreenStateReceiver.isEnable)
            ScreenStateReceiver.disable()
    }

    /**
     * 收集事件数据库流并更新通知和设置闹钟
     * @see AlarmManagerUtil.repeatDaily
     * @see updateAlarm
     * @see updateNoticeBuilder
     * */
    private fun setAlarmAndMainNotice() {
        //每天0点更新一下
        AlarmManagerUtil.repeatDaily(this, Calendar.getInstance().let { calendar ->
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time.time + 24 * 3600 * 1000
        })
        dataBase = Room.databaseBuilder(
            applicationContext,
            EventDataBase::class.java,
            EventDataBase.DATABASE_NAME
        )
            .enableMultiInstanceInvalidation().build()
        scope.launch {
            withContext(Dispatchers.IO) {
                dataBase.eventDao().getEvents().collect { events ->
                    val lastNotice = mainNotificationBuilder.toString()
                    val nextNoticeEvent = updateNoticeBuilder(events)
                    val newNotice = mainNotificationBuilder.toString()
                    updateAlarm(nextNoticeEvent)
                    //通知有变化才更新
                    if (newNotice != lastNotice) NotificationManagerCompat.from(this@CalendarNoticeService)
                        .notify(mainNoticeId, mainNotificationBuilder.build())
                }
            }
        }
    }

    /**
     * 更新通知建造器
     * @param events 所有事件
     * @return 下次提醒事件
     * */
    private fun updateNoticeBuilder(events: List<Event>): Event? {
        var nextDay: String? = null
        var nextEvent: Event? = null
        var nextNoticeEvent: Event? = null
        val nextDayEvents = mutableListOf<Event>()
        events.forEach {
            //获取展示的日期
            if (it.getEventTime() >= (Calendar.getInstance().let { calendar ->
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    calendar.time.time
                })) {
                if (nextDay == null) nextDay = it.day
                if (it.getEventTime() < nextDay!!.getTime()) nextDay = it.day

            }

            if (it.getEventTime() > Date().time) {
                //获取下次事件
                if (nextEvent == null) nextEvent = it
                if (it.getEventTime() < nextEvent!!.getEventTime()) nextEvent = it
                //获取下次提醒事件
                if (nextNoticeEvent == null) nextNoticeEvent = it
                if (it.getNoticeTime() < nextNoticeEvent!!.getNoticeTime()) nextNoticeEvent = it
            }
        }
        Log.i("下次提醒事件", nextNoticeEvent.toString())
        //获取该日期的所有事件
        events.forEach { event ->
            nextDay?.let {
                if (event.day == nextDay) nextDayEvents.add(event)
            }
        }
        //对展示的事件排序
        nextDayEvents.sortBy { event ->
            event.time.getTime()
        }
        mainNotificationBuilder.apply {
            subText = nextDay?.let {
                "$it 有${nextDayEvents.size}项日程"
            } ?: "无日程"
            contentTitle =
                when {
                    nextEvent == null -> "下一项：无"
                    nextEvent!!.day == Date().time.getTimeString("yyyy-MM-dd") -> "接下来：" + nextEvent!!.toEventString()
                    else -> "下一项：" + (nextEvent!!.day.getTime() - Calendar.getInstance()
                        .let { calendar ->
                            calendar.time = Date()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            calendar.time.time
                        }) / (1000 * 3600 * 24) + "天后 " + nextEvent!!.toEventString()
                }

            bigText = StringBuilder().let {
                nextDayEvents.forEach { event -> it.append("\n" + event.toEventString()) }
                if (it.isNotEmpty()) it.toString().substring(1)
                else ""
            }

        }
        return nextNoticeEvent
    }

    /**
     * 设置闹钟用以通知提醒和更新通知，和上次事件不同才会更新，更新会取消上次设置的事件闹钟
     * @Param nextNoticeEvent 下次提醒事件
     */
    private fun updateAlarm(nextNoticeEvent: Event?) {
        if (lastNoticeEvent.toString() == nextNoticeEvent.toString()) return
        lastNoticeEvent?.let {
            AlarmManagerUtil.cancel(this, it.getEventTime())
            AlarmManagerUtil.cancel(this, it.getNoticeTime())
        }
        lastNoticeEvent = nextNoticeEvent
        if (nextNoticeEvent == null) return
        //事件发生时刻都会有闹钟，用来更新通知内容
        AlarmManagerUtil.set(
            this@CalendarNoticeService,
            nextNoticeEvent.getEventTime(),
        )
        //设置提前提醒闹钟
        AlarmManagerUtil.set(
            this@CalendarNoticeService,
            nextNoticeEvent.getNoticeTime(),
        )
    }

    /**
     * 初始化通知建造器和通知渠道
     * */
    private fun initNotice() {
        mainNotificationBuilder = NotificationBuilder(this, mainChannelId)
        mainNotificationBuilder.builder
            .setSmallIcon(R.drawable.logo_notice)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setContentIntent(null)
        val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mainChannel =
            NotificationChannel(mainChannelId, mainChannelName, NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(mainChannel)
    }

    /**
     * 开启锁屏监听和重力传感器监听
     * */
    private fun startListener() {
        var openable = true
        val km = this.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        var time: Long = 0
        var isRunning = false
        SensorListener.apply {
            onTrigger = {
                it?.let {
                    if (!km.isKeyguardLocked) {
                        if (it.values[2] < -8) {
                            if (openable) {
                                VibratorUtil.vibrate(this@CalendarNoticeService, 100)
                                time = Date().time
                                startService(
                                    Intent().apply {
                                        setClassName(
                                            this@CalendarNoticeService,
                                            uiWebViewServiceName
                                        )
                                        action = ACTION_START_CALENDAR
                                    }
                                )
                                isRunning = true
                                openable = false
                            } else if (isRunning && Date().time - time > 500) {
                                isRunning = false
                                stopService(Intent().apply {
                                    setClassName(this@CalendarNoticeService, uiWebViewServiceName)
                                })
                            }
                        } else if (it.values[2] > 1) openable = true
                    }
                }
            }
            enable()
        }
        ScreenStateReceiver.apply {
            onScreenOn = {
                SensorListener.enable()
            }
            onScreenOff = {
                SensorListener.disable()
                stopService(Intent().apply {
                    setClassName(this@CalendarNoticeService, uiWebViewServiceName)
                })
            }
            enable()
        }
    }
}
