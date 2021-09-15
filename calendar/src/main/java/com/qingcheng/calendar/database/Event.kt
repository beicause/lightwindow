package com.qingcheng.calendar.database

import androidx.room.Entity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 事件实体对象
 * */
@Entity(
    primaryKeys = ["title", "time", "detail", "day", "color", "alarm"]
)
data class Event(
    val title: String = "",
    val time: String = "00:00:00",
    val detail: String = "",
    val day: String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()),
    val color: String = "#000000",
    val alarm: String = "0"
) {
    /**
     * @return 事件时刻的Calendar对象
     * */
    fun getCalendar(): Calendar {
        return Calendar.getInstance().apply {
            time = Date(getEventTime())
        }
    }

    /**
     * @return 返回格式 HH:mm title detail
     * */
    fun toEventString(): String {
        return "${time.substring(0, 5)} $title $detail"
    }

    /**
     * @return 事件时刻的时间戳
     * */
    fun getEventTime(): Long {
        return SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.CHINA
        ).parse("$day $time")!!.time
    }

    /**
     * @return 是否是系统闹钟事件
     * */
    fun isAlarmEvent(): Boolean {
        return alarm.substring(0, 1) == "*"
    }

    /**
     * @return 提醒时间，为（事件时间 - 提前时间）
     * */
    fun getNoticeTime(): Long {
        return getEventTime() - (if (isAlarmEvent()) alarm.substring(1) else alarm).toLong() * 60000
    }

    /**
     * @return 事件对象的JSON格式
     * */
    override fun toString(): String {
        return """{
            "title": "$title",
            "time": "$time",
            "detail": "$detail",
            "day": "$day",
            "color": "$color",
            "alarm": "$alarm",
        }""".trimIndent()
    }
}

/**
 * yyyy-MM-dd 或 HH:mm:ss 或 yyyy-MM-dd HH:mm:ss格式字符串转时间戳
 * */
fun String.getTime(): Long {
    return when {
        this.matches(Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")) -> {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(this)!!.time
        }
        this.matches(Regex("""\d{4}-\d{2}-\d{2}""")) -> {
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(this)!!.time
        }
        this.matches(Regex("""\d{2}:\d{2}:\d{2}""")) -> {
            val ts = this.split(":")
            ts[0].toLong() * 3600 + ts[1].toLong() * 60 + ts[2].toLong()
        }
        else -> throw ParseException("日期格式错误", 0)
    }
}

/**
 * 时间戳转日期格式字符串，yyyy-MM-dd 或 HH:mm:ss 或 yyyy-MM-dd HH:mm:ss格式
 * */
fun Long.getTimeString(pattern: String): String {
    return if (pattern == "HH:mm:ss") {
        val s = (this % 3600) % 60
        val m = ((this % 3600) - s) / 60
        val h = (this - s - m) / 3600
        (if (h / 10 < 1) "0$h:" else "$h:") + (if (m / 10 < 1) "0$m:" else "$m:") + (if (s / 10 < 1) "0$s" else "$s")
    } else SimpleDateFormat(pattern, Locale.CHINA).format(Date(this))
}
