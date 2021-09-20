package com.qingcheng.calendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.qingcheng.base.cache.CacheName
import com.qingcheng.base.cache.CacheName.MAIN_HEIGHT
import com.qingcheng.base.cache.CacheName.MAIN_WIDTH
import com.qingcheng.base.util.ScreenUtil
import com.qingcheng.base.util.SharedPreferencesUtil
import com.qingcheng.base.util.ToastUtil
import com.qingcheng.base.view.BaseFloatWindow
import com.qingcheng.calendar.R
import com.qingcheng.calendar.database.Event
import com.qingcheng.calendar.database.getTime
import com.qingcheng.calendar.database.getTimeString
import com.qingcheng.calendar.service.AlarmManagerUtil
import com.qingcheng.calendar.service.CalendarWindowService
import com.qingcheng.calendar.service.CalendarWindowService.Companion.dataBase
import com.qingcheng.calendar.util.csust.CsustRequest
import com.qingcheng.calendar.util.gnnu.GnnuRequest
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
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
class CalendarView(private val context: Context) :
    BaseFloatWindow<View>(context, View.inflate(context, R.layout.calendar, null)) {
    private val jsInterfaceName = "Android"

    init {
        applyParams {
            width =
                if (SharedPreferencesUtil.getInt(
                        context,
                        MAIN_WIDTH.name
                    ) == 0
                ) 350.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_WIDTH.name)
            height =
                if (SharedPreferencesUtil.getInt(context, MAIN_HEIGHT.name) == 0)
                    620.toIntDip()
                else SharedPreferencesUtil.getInt(context, MAIN_HEIGHT.name)
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
                            if (it == "null") throwError("页面异常")
                            SharedPreferencesUtil.put(
                                context,
                                CacheName.WEB_VERSION.name,
                                it
                            )
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        throwError(error?.description.toString())
                        super.onReceivedError(view, request, error)
                    }
                }
                val close: () -> Unit = {
                    post {
                        applyParams {
                            if (ScreenUtil.isLandscape(context)) {
                                val t = width
                                width = height
                                height = t
                            }
                            SharedPreferencesUtil.put(context, MAIN_WIDTH.name, width)
                            SharedPreferencesUtil.put(context, MAIN_HEIGHT.name, height)
                        }
                        rotateOut()
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
                addJavascriptInterface(
                    JsInterface(
                        context, mapOf(
                            "close" to close
                        )
                    ), jsInterfaceName
                )
                loadUrl("https://qingcheng.asia/cld")
            }
        }
    }

    private fun throwError(message: String = "") {
        view.findViewById<WebView>(R.id.wv_cld).destroy()
        this@CalendarView.view.visibility = View.GONE
        ToastUtil.showToast("加载失败：$message")
        view.handler.postDelayed({
            context.stopService(
                Intent(
                    context,
                    CalendarWindowService::class.java
                )
            )
        }, 1500)
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
                    context.stopService(Intent(context, CalendarWindowService::class.java))
                }
            }
        }
    }

    class JsInterface(
        private val context: Context,
        private val other: Map<String, () -> Unit>? = null
    ) {
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
        fun close() {
            other?.get("close")?.invoke()
        }

        @JavascriptInterface
        fun addEvents(events: String) {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    Log.i("数据库增加", events)
                    dataBase?.eventDao()?.addEvents(stringToList(events))
                }
            }
        }

        @JavascriptInterface
        fun removeEvents(events: String) {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    Log.i("数据库移除", events)
                    dataBase?.eventDao()?.removeEvents(stringToList(events))
                }
            }
        }

        @JavascriptInterface
        fun getEvents(): String {
            val el: List<Event>
            runBlocking {
                withContext(Dispatchers.IO) {
                    el = dataBase?.eventDao()?.getEvents()?.first()
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
                        val alarmEvents = events.filter {
                            it.alarm.substring(
                                0,
                                1
                            ) == "*" && it.getEventTime() > Date().time
                        }
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
                            withContext(Dispatchers.Main) {
                                ToastUtil.showToast("已设置系统闹钟，若要取消，请手动前往")
                            }
                            //延迟一段时间，否则会设置失败
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
            var el: List<Event>? = null
            runBlocking {
                el = GnnuRequest.getGnnuSchedule(username, password)
            }
            Log.i("gnnu", el.toString())
            return listToString(el!!)
        }
    }
}