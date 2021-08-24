package com.qingcheng.lightwindow.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.qingcheng.lightwindow.R
import com.qingcheng.lightwindow.cache.CacheName
import com.qingcheng.lightwindow.cache.CacheName.CACHE_MAIN_HEIGHT
import com.qingcheng.lightwindow.cache.CacheName.CACHE_MAIN_WIDTH
import com.qingcheng.lightwindow.database.Event
import com.qingcheng.lightwindow.database.getTime
import com.qingcheng.lightwindow.database.getTimeString
import com.qingcheng.lightwindow.service.AlarmManagerUtil
import com.qingcheng.lightwindow.service.CalendarWindowService
import com.qingcheng.lightwindow.service.CalendarWindowService.Companion.dataBase
import com.qingcheng.lightwindow.showToast
import com.qingcheng.lightwindow.util.ScreenDisplayUtil
import com.qingcheng.lightwindow.util.SharedPreferencesUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
/**
 * 日程表悬浮窗类
 * */
@SuppressLint("SetJavaScriptEnabled")
class CalendarView(context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.calendar, null)) {
    private val jsInterfaceName = "Android"
    var stopService = {}

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(
                        context,
                        CACHE_MAIN_WIDTH.keyName
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, CACHE_MAIN_WIDTH.keyName)
            height =
                if (SharedPreferencesUtil.getInt(context, CACHE_MAIN_HEIGHT.keyName) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, CACHE_MAIN_HEIGHT.keyName)
        }
        applyView {
            findViewById<WebView>(R.id.wv_cld).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        evaluateJavascript("javascript:getVersion()") {
                            Log.i("cld版本", it)
                            SharedPreferencesUtil.put(
                                context,
                                CacheName.CACHE_CLD_VERSION.keyName,
                                it
                            )
                        }
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.apply {
                            Log.i(
                                this@CalendarView::class.qualifiedName,
                                "${message()} -- From line ${lineNumber()} of ${sourceId()}"
                            )
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }
                }
                addJavascriptInterface(JsInterface(context), jsInterfaceName)
                loadUrl("https://qingcheng.asia/cld/")
            }
            findViewById<ImageView>(R.id.iv_cld_close).setOnClickListener {
                applyParams {
                    if (ScreenDisplayUtil.isLandscape(context)) {
                        val t = width
                        width = height
                        height = t
                    }
                    SharedPreferencesUtil.put(context, CACHE_MAIN_WIDTH.keyName, width)
                    SharedPreferencesUtil.put(context, CACHE_MAIN_HEIGHT.keyName, height)
                }
                rotateOut()
            }

            findViewById<ImageView>(R.id.iv_zoom).setOnClickListener {
                it.visibility = View.GONE
                ViewManager.new(::ZoomView, context).apply {
                    addToWindow()
                    view.visibility=View.VISIBLE
                    setPosition(0,0)
                    view.post {
                        moveTo(
                            toX = (ScreenDisplayUtil.getWidth(context) - view.width) / 2,
                            toY = (ScreenDisplayUtil.getHeight(context) - view.height) / 2
                        )
                    }
                }
            }
        }
    }

    fun rotateIn() {
        applyView {
            visibility = View.VISIBLE
            rotationY = 90f
            addToWindow()
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 0f).apply {
                spring.stiffness = SpringForce.STIFFNESS_LOW
                start()
            }
        }
    }

    fun rotateOut() {
        applyView {
            SpringAnimation(this, DynamicAnimation.ROTATION_Y, 90f).apply {
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
                addEndListener { _, _, _, _ ->
                    visibility = View.INVISIBLE
                    this@CalendarView.removeFromWindow()
                    view.findViewById<WebView>(R.id.wv_cld).destroy()
                    this@CalendarView.stopService()
                }
            }
        }
    }

    class JsInterface(private val context: Context) {
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

        @JavascriptInterface
        fun addEvents(events: String) {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    Log.i("数据库增加", events)
                    CalendarWindowService.dataBase?.eventDao()?.addEvents(stringToList(events))
                }
            }
        }

        @JavascriptInterface
        fun removeEvents(events: String) {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    Log.i("数据库移除", events)
                    CalendarWindowService.dataBase?.eventDao()?.removeEvents(stringToList(events))
                }
            }
        }

        @JavascriptInterface
        fun getEvents(): String {
            val el: List<Event>
            runBlocking {
                withContext(Dispatchers.IO) {
                    el = CalendarWindowService.dataBase?.eventDao()?.getEvents()?.first()
                        ?: emptyList()
                }
            }
            Log.i("获取数据库", el.toString())
            return listToString(el)
        }

        /**
         * 设置系统闹钟
         * @see AlarmManagerUtil.setAlarmClock
         * */
        @JavascriptInterface
        fun setSystemAlarm() {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    dataBase?.eventDao()?.getAlarmEvents()?.first()?.let { events ->
                        //过滤出闹钟事件，要求时间晚于现在
                        val alarmEvents = events.filter { it.alarm.substring(0, 1) == "*"&&it.getEventTime()>Date().time }
                        //此map格式 key为title to (time+alarm), value为days数组
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
                            showToast("已设置系统闹钟，若要取消，请手动前往")
                            //延迟一段时间，否则会设置失败
                            delay(100)
                        }
                    }
                }
            }
        }
    }
}