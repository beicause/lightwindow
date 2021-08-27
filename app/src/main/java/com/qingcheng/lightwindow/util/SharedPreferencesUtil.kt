package com.qingcheng.lightwindow.util

import android.content.Context
import android.content.SharedPreferences
import com.qingcheng.lightwindow.cache.SpName
import org.json.JSONObject
/**
 * SharedPreferences工具
 * @see SharedPreferences
 * */
object SharedPreferencesUtil {
    //默认的sp文件名
    private val defaultName:String= SpName.SP_NAME_CACHE.name
    /**
     * sharedPreference存入键值对
     * @param context
     * @param key 键
     * @param value 值
     * @param name 存入的sharedPreference文件名
     * */
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