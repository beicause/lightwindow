package com.qingcheng.calendar

import com.qingcheng.calendar.util.gnnu.GnnuRequest
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

    }
}

fun main() {
    runBlocking { GnnuRequest.getGnnuSchedule("", "") }
}

