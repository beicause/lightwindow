package com.qingcheng.calendar.util.csust

import com.qingcheng.calendar.database.Event
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

object CsustRequest {
    private val dispatcher = Dispatchers.IO
    suspend fun getCsustEvents(username: String, password: String): List<Event> {
        val u = Base64.getEncoder().encodeToString(username.toByteArray())
        val p = Base64.getEncoder().encodeToString(password.toByteArray())
        val encoded = "$u%%%$p"
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val list = mutableListOf<Event>()
        Calendar.getInstance().apply {
            val dayOfYear = get(Calendar.DAY_OF_YEAR)
            coroutineScope {
                val client = OkHttpClient().newBuilder().followRedirects(false).build()
                val kc = mutableListOf<Deferred<Unit>>()
                val cookie = getCookie(encoded, client)
//                println(cookie)
                for (i in if (dayOfYear >= 235) 0 until 25 else -25..0) {
                    kc.add(async {
                        set(Calendar.DAY_OF_YEAR, 235 + i * 7)
                        val l = getDaySchedule(
                            cookie,
                            formatter.format(time),
                            client
                        )
                        list.addAll(l)
                        Unit
                    })
                }
                kc.awaitAll()
            }
        }
//        println(list.filter { e->e.title=="绿色建材(自然科学)" })
        return list
    }

    private suspend fun getCookie(encoded: String, client: OkHttpClient): String {
        return withContext(dispatcher) {
            client.newCall(
                Request.Builder()
                    .url("http://xk.csust.edu.cn/jsxsd/xk/LoginToXk")
                    .post(FormBody.Builder().add("encoded", encoded).build())
                    .build()
            ).execute().let {
                it.headers["Set-Cookie"] ?: throw NullPointerException("cookie is null")
            }
        }
    }

    private suspend fun getDaySchedule(
        cookie: String,
        day: String,
        client: OkHttpClient
    ): List<Event> {
        return withContext(dispatcher) {
            client.newCall(
                Request.Builder()
                    .url("http://xk.csust.edu.cn/jsxsd/framework/main_index_loadkb.jsp?rq=$day")
                    .method("POST", "".toRequestBody())
                    .addHeader("Cookie", cookie)
                    .build()
            ).execute().body?.string()?.let { html ->
                parseResponse(html, day)
            } ?: throw NullPointerException("schedule html is null")
        }
    }

    /**
     * 获取课程
     */
    private fun parseResponse(html: String, day: String): List<Event> {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        calendar.time = formatter.parse(day)!!
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        val document: Document = Jsoup.parse(html)
        val elements: Elements = document.getElementsByTag("td")
        if (elements.size == 0) return emptyList()
        val list = mutableListOf<Event>()
        for (i in 0 until elements.size) {
            if (elements[i].childrenSize() == 0) continue
            calendar.set(Calendar.DAY_OF_WEEK, i % 8)
            val time = when (i / 8) {
                0 -> "08:00:00"
                1 -> "10:10:00"
                2 -> "14:00:00"
                3 -> "16:10:00"
                4 -> "19:30:00"
                else -> continue
            }
            val classInfo = elements[i].child(0).attr("title").split("<br/>")
            if (classInfo.size < 5) continue
            val title = classInfo[2].substring(5)
            val detail = classInfo[4].substring(5)
            list.add(Event(title, time, detail, formatter.format(calendar.time)))
        }
        return list
    }
}