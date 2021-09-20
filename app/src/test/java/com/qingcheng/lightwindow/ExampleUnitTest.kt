package com.qingcheng.lightwindow

import org.json.JSONObject
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //print(SharedPreferencesUtil.getString(this,""))
        //    val client=OkHttpClient()
//    val res=client.newCall(Request.Builder()
//        .url("https://smile.huii.top/user/login")
//        .post(
//            FormBody.Builder()
//            .add("username","")
//            .add("password","")
//            .build()
//        )
//        .build()).execute().body?.string()
//    print(res)
        val m = mapOf("l" to "x", "s" to "s")
        val s = JSONObject(m)
        print(s)
        // print(JSONArray(listOf(A("aaa","aa"),A("aaa","aa"))).toString())

    }
}