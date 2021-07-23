package com.qingcheng.floatwindow.util

import android.content.Context
import com.qingcheng.floatwindow.util.CacheName.SP_NAME_CACHE
import com.qingcheng.floatwindow.util.CacheName.SP_NAME_SETTINGS
import org.json.JSONObject

object SharedPreferencesUtil {

    fun put(context: Context, key: String, value: String) {
        context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }
    fun put(context: Context, key: String, value: Int) {
        context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun put(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply()
    }
    fun remove(context: Context,vararg keys: String){
        keys.forEach { context.getSharedPreferences(SP_NAME_CACHE,Context.MODE_PRIVATE).edit().remove(it).apply()}
    }

    fun getString(context: Context, key: String): String? {
        return context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).getString(key, null)
    }

    fun getInt(context: Context, key: String): Int {
        return context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).getInt(key, 0)
    }

    fun getBoolean(context: Context, key: String): Boolean {
        return context.getSharedPreferences(SP_NAME_CACHE, Context.MODE_PRIVATE).getBoolean(key, false)
    }
    fun contains(context: Context,key: String):Boolean{
        return context.getSharedPreferences(SP_NAME_CACHE,Context.MODE_PRIVATE).contains(key)
    }

    fun getAllData(context: Context):String{
        val cache=context.getSharedPreferences(SP_NAME_CACHE,Context.MODE_PRIVATE).all
        val settings=context.getSharedPreferences(SP_NAME_SETTINGS,Context.MODE_PRIVATE).all
        return "{\"${SP_NAME_CACHE}\":${JSONObject(cache)},\"$SP_NAME_SETTINGS\":${JSONObject(settings)}}"
    }
}