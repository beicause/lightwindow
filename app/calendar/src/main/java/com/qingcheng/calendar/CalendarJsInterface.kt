package com.qingcheng.calendar

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import com.qingcheng.base.*
import com.qingcheng.base.util.PreferencesUtil
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.BaseFloatWindow
import com.qingcheng.calendar.database.Event
import com.qingcheng.calendar.database.EventDataBase
import com.qingcheng.calendar.database.getTime
import com.qingcheng.calendar.database.getTimeString
import com.qingcheng.calendar.service.AlarmManagerUtil
import com.qingcheng.calendar.service.CalendarNoticeService
import com.qingcheng.calendar.util.csust.CsustRequest
import com.qingcheng.calendar.util.gnnu.GnnuRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CalendarJsInterface(
    private val context: Context,
    private val floatWindow: BaseFloatWindow<*>,
    private val dataBase: EventDataBase,
) {

    @JavascriptInterface
    fun redirectToMain() {
        context.startService(Intent().apply {
            setClassName(context, uiWebViewServiceName)
            action = ACTION_START_MAIN
        })
    }

    @JavascriptInterface
    fun redirectToCalendar() {
        context.startService(Intent().apply {
            setClassName(context, uiWebViewServiceName)
            action = ACTION_START_CALENDAR
        })
    }

    @JavascriptInterface
    fun close() {
        floatWindow.view.post {
            floatWindow.applyParams {
                if (ScreenUtil.isLandscape(context)) {
                    val t = width
                    width = height
                    height = t
                }
                PreferencesUtil.putString(context, MAIN_WIDTH, width.toString())
                PreferencesUtil.putString(context, MAIN_HEIGHT, height.toString())
            }
            floatWindow.rotateOut {
                context.stopService(Intent().apply {
                    setClassName(context, uiWebViewServiceName)
                })
            }
        }
    }

//    @JavascriptInterface
//    fun exception(s: String) {
//        runOnUI { ToastUtil.showToast(s, isLong = true) }
//        floatWindow.rotateOut()
//        Handler(Looper.getMainLooper()).postDelayed(3000) {
//            context.stopService(Intent().apply {
//                setClassName(context, uiWebViewServiceName)
//            })
//        }
//    }

    @JavascriptInterface
    fun addEvents(events: String) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                Log.i("???????????????", events)
                dataBase.eventDao().addEvents(stringToList(events))
            }
        }
    }

    @JavascriptInterface
    fun removeEvents(events: String) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                Log.i("???????????????", events)
                dataBase.eventDao().removeEvents(stringToList(events))
            }
        }
    }

    @JavascriptInterface
    fun getEvents(): String {
        val el: List<Event>
        runBlocking {
            withContext(Dispatchers.IO) {
                el = dataBase.eventDao().getEvents().first()
            }
        }
        Log.i("???????????????", el.toString())
        return listToString(el)
    }

    /**
     * ??????????????????
     * @see AlarmManagerUtil.setAlarmClock
     * */
    @JavascriptInterface
    fun setSystemAlarm() {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                dataBase.eventDao().getAlarmEvents().first()
                    .let { events ->
                        //????????????????????????????????????????????????
                        val alarmEvents = events.filter {
                            it.alarm.substring(
                                0,
                                1
                            ) == "*" && it.getEventTime() > Date().time
                        }
                        //???map?????? key???title to (time+alarm), value???days??????
                        val map = mutableMapOf<Pair<String, String>, ArrayList<Int>>()
                        alarmEvents.forEach {
                            val k = it.title to (it.time + it.alarm)
                            if (map.containsKey(k)) map[k]?.add(
                                it.getCalendar().get(Calendar.DAY_OF_WEEK)
                            )
                            else map[k] = ArrayList()
                        }
                        map.forEach { (k, v) ->
                            val t = k.second.split("*")
                            val mills = t[0].getTime() - t[1].toLong() * 60
                            AlarmManagerUtil.setAlarmClock(
                                context,
                                k.first,
                                mills.getTimeString("HH:mm:ss"),
                                v
                            )
                            withContext(Dispatchers.Main) {
                                ToastUtil.showToast("??????????????????????????????????????????????????????")
                            }
                            //??????????????????????????????????????????
                            delay(100)
                        }
                    }
            }
        }
    }

    @JavascriptInterface
    fun requestCsustEvents(username: String, password: String): String {
        val el: List<Event>
        runBlocking { el = CsustRequest.getCsustEvents(username, password) }
        Log.i("csust", el.toString())
        return listToString(el)
    }

    @JavascriptInterface
    fun requestGnnuEvents(username: String, password: String): String {
        var el: List<Event>?
        runBlocking {
            el = GnnuRequest.getGnnuSchedule(username, password)
        }
        Log.i("gnnu", el.toString())
        return listToString(el!!)
    }

    @JavascriptInterface
    fun getPolicy(): String =
        PreferencesUtil.getString(context, POLICY) ?: "null"


    @JavascriptInterface
    fun setPolicy(value: String) {
        PreferencesUtil.putString(context, POLICY, value)
    }

    @JavascriptInterface
    fun setEnableSensor(isEnable: String) {
        PreferencesUtil.putString(context, ENABLE_SENSOR, isEnable)
        context.startService(Intent(context, CalendarNoticeService::class.java).apply {
            action = CalendarNoticeService.SENSOR_CHANGE_ACTION
            putExtra("sensor", isEnable == "true")
        })
    }

    @JavascriptInterface
    fun getEnableSensor(): String =
        PreferencesUtil.getString(context, ENABLE_SENSOR).toString()

    private fun stringToList(events: String): List<Event> {
        val eventsList: MutableList<Event> = mutableListOf()
        val es = JSONArray(events)
        for (i in (0 until es.length())) {
            val e = es.getJSONObject(i)
            eventsList.add(
                Event(
                    e.getString("title"),
                    e.getString("time"),
                    e.getString("detail"),
                    e.getString("day"),
                    e.getString("color"),
                    e.getString("alarm")
                )
            )
        }
        return eventsList
    }

    private fun listToString(events: List<Event>): String {
        val eventArray = JSONArray()
        events.forEach {
            val event = JSONObject().apply {
                put("title", it.title)
                put("time", it.time)
                put("day", it.day)
                put("detail", it.detail)
                put("alarm", it.alarm)
                put("color", it.color)
            }
            eventArray.put(event)
        }
        return eventArray.toString()
    }
}
