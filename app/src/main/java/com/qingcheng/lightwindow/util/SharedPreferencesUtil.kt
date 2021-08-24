package com.qingcheng.lightwindow.util

import android.content.Context
import android.content.SharedPreferences
import com.qingcheng.lightwindow.cache.SpName
import org.json.JSONObject
/**
 * sp工具类
 * @see SharedPreferences
 * */
object SharedPreferencesUtil {
    private val defaultName:String= SpName.SP_NAME_CACHE.name

    fun put(context: Context, key: String, value: String,name:String=defaultName) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }
    fun put(context: Context, key: String, value: Int,name: String=defaultName) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun put(context: Context, key: String, value: Boolean,name: String=defaultName) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply()
    }
    fun remove(context: Context, name: String=defaultName, vararg keys: String){
        keys.forEach { context.getSharedPreferences(name,Context.MODE_PRIVATE).edit().remove(it).apply()}
    }

    fun getString(context: Context, key: String,name: String=defaultName): String {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, "null")!!
    }

    fun getInt(context: Context, key: String,name: String=defaultName): Int {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, 0)
    }

    fun getBoolean(context: Context, key: String,name: String=defaultName): Boolean {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(key, false)
    }
    fun contains(context: Context,key: String,name: String=defaultName):Boolean{
        return context.getSharedPreferences(name,Context.MODE_PRIVATE).contains(key)
    }

    fun getAllData(context: Context):String{
        val cache=context.getSharedPreferences(SpName.SP_NAME_CACHE.name,Context.MODE_PRIVATE).all
        return "{\"${SpName.SP_NAME_CACHE.name}\":${JSONObject(cache)}}"
    }
}