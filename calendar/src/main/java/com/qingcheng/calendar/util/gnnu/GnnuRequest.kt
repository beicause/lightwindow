package com.qingcheng.calendar.util.gnnu

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.Cipher

object GnnuRequest {

    suspend fun getGnnuSchedule(username: String, password: String) {
        val cookie = login(username, password)
        val calendar = Calendar.getInstance()
        val xqm = if (calendar.get(Calendar.MONTH) >= 8) 3 else 12
        val res = withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151&su=$username")
                    .post(
                        FormBody.Builder()
                            .add("xqm", "" + xqm)
                            .add("xnm", "" + calendar.get(Calendar.YEAR))
                            .add("kzlx", "ck")
                            .build()
                    )
                    .build()
            ).execute()
        }
        println(res.body?.string())
    }

    private suspend fun login(username: String, password: String): String {
        val csrftoken = "e70b4137-6f70-425f-b9ce-ec7f4b572039,e70b41376f70425fb9ceec7f4b572039"
        val data = getPublicKey()
        val enPassword = encodePassword(data, password)
        return withContext(Dispatchers.IO) {
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
            ).execute().let {
                it.headers["Set-Cookie"]!!.split(";")[0] + ";" + data.cookie.split(";")[1]
            }
        }
    }

    data class PublicData(val modulus: String, val exponent: String, val cookie: String)

    private suspend fun getPublicKey(): PublicData {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            client.newCall(
                Request.Builder()
                    .url("http://jwgl.gnnu.cn/jwglxt/xtgl/login_getPublicKey.html?time=${Date().time}&_=${Date().time}")
                    .get()
                    .build()
            ).execute().let {
                val arr = it.body?.string()?.split("\"") ?: throw IOException("public key is null")
                val m = arr[3]
                val e = arr[7]
                val cs = it.headers.values("Set-Cookie")
                val c = cs[0].split(";")[0] + "; " + cs[1].split(";")[0]
                println(m)
                println(e)
                println(c)
                PublicData(m, e, c)
            }
        }
    }


    private fun encodePassword(data: PublicData, password: String): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(
            Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA")
                .generatePublic(
                    RSAPublicKeySpec(
                        BigInteger(b64tohex(data.modulus), 16),
                        BigInteger(b64tohex(data.exponent), 16)
                    )
                )
        )
        val enPassword = Base64.getEncoder().encodeToString(cipher.doFinal(password.toByteArray()))
        println(enPassword)
        return enPassword
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