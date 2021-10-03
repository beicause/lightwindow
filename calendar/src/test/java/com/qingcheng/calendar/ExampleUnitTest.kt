package com.qingcheng.calendar

import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*


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
    runBlocking {
        val cld = Calendar.getInstance(Locale.CHINA)
        println(cld.get(Calendar.DAY_OF_WEEK))
    }
}

