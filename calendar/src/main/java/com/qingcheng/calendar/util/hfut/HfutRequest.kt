//package com.qingcheng.calendar.util.hfut
//
//import android.annotation.SuppressLint
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.util.*
//import javax.crypto.Cipher
//import javax.crypto.spec.SecretKeySpec
//
//object HfutRequest {
//    fun getKey(): String {
//        val client = OkHttpClient()
//        val res = client.newCall(
//            Request.Builder()
//                .get()
//                .url("https://cas.hfut.edu.cn/cas/checkInitVercode?_=${Date().time}")
//                .build()
//        ).execute()
//        return res.headers("Set-Cookie").joinToString("; ")
//    }
//
//    @SuppressLint("GetInstance")
//    fun encodePassword(key: String, password: String): String {
//        val cipher = Cipher.getInstance("AES")
//        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
//        val bytes = cipher.doFinal(password.toByteArray())
//        return Base64.getEncoder().encodeToString(bytes)
//    }
//
//    fun checkUserIdentity(username: String, password: String): String? {
//        val keyCookie = getKey()
//        println(keyCookie)
//        val tk = keyCookie.split(";").filter { it.matches(Regex("""LOGIN_FLAVORING=.*""")) }
//        if (tk.size != 1) throw Exception("key error")
//        val key = tk[0].substring(16)
//        val enPassword =encodePassword(key, password)
//        println(key)
//        println(enPassword)
//        val client = OkHttpClient()
//        val res = client.newCall(
//            Request.Builder()
//                .addHeader("Cookie", keyCookie)
//                .get()
//                .url("https://cas.hfut.edu.cn/cas/policy/checkUserIdenty?username=$username&password=$enPassword&_=${Date().time}")
//                .build()
//        ).execute()
//        println(res.request.headers)
//        println(res.body?.string())
//        return res.headers["Set-Cookie"]
//    }
//}