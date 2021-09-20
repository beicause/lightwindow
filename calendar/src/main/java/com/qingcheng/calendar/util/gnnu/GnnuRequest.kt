package com.qingcheng.calendar.util.gnnu

import com.qingcheng.calendar.database.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

import org.jsoup.Jsoup
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher

object GnnuRequest {
    private val dispatcher = Dispatchers.IO
    suspend fun getGnnuSchedule(username: String, password: String): List<Event> {
        val cookie = login(username, password)
        println(cookie)
        val calendar = Calendar.getInstance()
        val xqm = if (calendar.get(Calendar.MONTH) >= 8) 3 else 12
        val json = withContext(dispatcher) {
            val client = OkHttpClient().newBuilder().followRedirects(false).build()
            client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151&su=$username")
                    .addHeader("Cookie", cookie)
                    .post(
                        FormBody.Builder()
                            .add("xqm", "" + xqm)
                            .add("xnm", "" + calendar.get(Calendar.YEAR))
                            .add("kzlx", "ck")
                            .build()
                    )
                    .build()
            ).execute().body?.string() ?: throw NullPointerException("schedule json is null")
        }
        println(json)
        return parseJson(json, getTermDate(username, cookie))
    }

    private fun parseJson(s: String, start: Date): List<Event> {
        val kbList = JSONObject(s).getJSONArray("kbList")
        val list = mutableListOf<Event>()
        for (i in 0 until kbList.length()) {
            val k = kbList.getJSONObject(i)
            val title = k.getString("kcmc")
            val detail = k.getString("cdmc")
            val xqj = k.getInt("xqj")
            val time = when (k.getString("jc").split("-")[0]) {
                "1" -> "08:20"
                "2" -> "09:10"
                "3" -> "10:15"
                "4" -> "11:05"
                "5" -> "14:00"
                "6" -> "14:50"
                "7" -> "15:55"
                "8" -> "16:45"
                "9" -> "19:10"
                "10" -> "20:00"
                "11" -> "20:50"
                else -> throw Exception("course time error")
            } + ":00"

            val startWeek: Int
            val endWeek: Int
            k.getString("zcd").split("-").let {
                startWeek = it[0].toInt()
                endWeek = it[1].substring(0, it[1].length - 1).toInt()
            }
            val calendar = Calendar.getInstance()
            for (w in startWeek..endWeek) {
                calendar.time = start
                calendar.set(Calendar.DAY_OF_WEEK, if (xqj == 7) 0 else xqj)
                calendar.add(Calendar.WEEK_OF_YEAR, w - 1)
                val day = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.time)
                list.add(Event(title, time, detail, day))
            }
        }
        return list
    }

    private suspend fun getTermDate(username: String, cookie: String): Date {
        return withContext(dispatcher) {
            val client = OkHttpClient().newBuilder().followRedirects(false).build()
            val html = client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/xtgl/index_cxAreaFive.html?localeKey=zh_CN&gnmkdm=index&su=$username")
                    .addHeader("Cookie", cookie)
                    .post(FormBody.Builder().build())
                    .build()
            ).execute().body?.string() ?: throw NullPointerException("term date is null")
            val elements = Jsoup.parse(html)
                .getElementsContainingText("" + Calendar.getInstance().get(Calendar.YEAR))
            val date = elements[0].text().split("(")
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(
                date[1].split("至")[0]
            ) ?: throw NullPointerException("date parse error")
        }
    }

    private suspend fun login(username: String, password: String): String {
        val csrftoken = "e70b4137-6f70-425f-b9ce-ec7f4b572039,e70b41376f70425fb9ceec7f4b572039"
        println("---------GET-KEY-----------")
        val data = getPublicKey()
        println(data.modulus)
        println(data.exponent)
        println(data.cookie)
        println("----------------------")
        val enPassword = encodePassword(data, password)
        println("----------START LOGIN---------------")
        println(data.cookie)
        println(username)
        println(enPassword)
        println("----------------------")
        return withContext(dispatcher) {
            val client = OkHttpClient().newBuilder().followRedirects(false).build()
            client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/xtgl/login_slogin.html?time=${Date().time}")
                    .addHeader("Cookie", data.cookie)
                    .post(
                        FormBody.Builder()
                            .add("csrftoken", csrftoken)
                            .add("yhm", username)
                            .add("mm", enPassword)
                            .add("mm", enPassword)
                            .add("language", "zh_CN")
                            .build()
                    )
                    .build()
            ).execute().let { response ->
                val setCookies = response.headers.values("Set-Cookie")
                println(setCookies)
                val cookie = setCookies.filter { !it.matches(Regex("""rememberMe(.*)""")) }
                if (cookie.size != 1) throw java.lang.Exception("账号或密码错误")
                cookie[0].split(";")[0] + ";" + data.cookie.split(";")[1]
            }
        }
    }

    data class PublicData(val modulus: String, val exponent: String, val cookie: String)

    private suspend fun getPublicKey(): PublicData {
        return withContext(dispatcher) {
            val client = OkHttpClient().newBuilder().followRedirects(false).build()
            client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/xtgl/login_getPublicKey.html?time=${Date().time}&_=${Date().time}")
                    .get()
                    .build()
            ).execute().let {
                val arr = it.body?.string()?.split("\"")
                    ?: throw NullPointerException("public key is null")
                val m = arr[3]
                val e = arr[7]
                val cs = it.headers.values("Set-Cookie")
                val c = cs[0].split(";")[0] + "; " + cs[1].split(";")[0]
                PublicData(m, e, c)
            }
        }
    }


    private fun encodePassword(data: PublicData, password: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA")
                .generatePublic(
                    RSAPublicKeySpec(
                        BigInteger(b64tohex(data.modulus), 16),
                        BigInteger(b64tohex(data.exponent), 16)
                    )
                )
        )
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.toByteArray()))
    }

//    private fun ByteArray.toHexString():String{
//        val sb=StringBuilder()
//        for (i in this){
//            val hex:String=Integer.toHexString(0xff and i.toInt())
//            if(hex.length==1) sb.append("0")
//            sb.append(hex)
//        }
//        return sb.toString()
//    }

    private fun b64tohex(s: String): String {
        val b64map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val b64pad = '='
        var ret = ""
        var k = 0 // b64 state, 0-3
        var slop = 0
        for (i in s.indices) {
            if (s[i] == b64pad) break
            val v = b64map.indexOf(s[i])
            if (v < 0) continue
            when (k) {
                0 -> {
                    ret += int2char(v shr 2)
                    slop = v and 3
                    k = 1
                }
                1 -> {
                    ret += int2char((slop shl 2) or (v shr 4))
                    slop = v and 0xf
                    k = 2
                }
                2 -> {
                    ret += int2char(slop)
                    ret += int2char(v shr 2)
                    slop = v and 3
                    k = 3
                }
                else -> {
                    ret += int2char((slop shl 2) or (v shr 4))
                    ret += int2char(v and 0xf)
                    k = 0
                }
            }
        }
        if (k == 1)
            ret += int2char(slop shl 2)
        return ret
    }

    private fun int2char(i: Int): Char {
        val biRm = "0123456789abcdefghijklmnopqrstuvwxyz"
        return biRm[i]
    }
}