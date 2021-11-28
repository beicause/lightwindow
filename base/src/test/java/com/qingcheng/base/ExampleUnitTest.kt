package com.qingcheng.base

import com.qingcheng.base.util.NetworkRequestUtil
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
        runBlocking {
            val a = NetworkRequestUtil.getVersion().body?.string()
            println(a)
        }

    }
}

fun main() {

}

