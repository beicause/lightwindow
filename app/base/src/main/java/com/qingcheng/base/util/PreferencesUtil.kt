package com.qingcheng.base.util

import android.content.ContentValues
import android.content.Context
import com.qingcheng.base.provider.CacheDataBase.Companion.KEY
import com.qingcheng.base.provider.CacheDataBase.Companion.VALUE
import com.qingcheng.base.provider.CacheProvider

object PreferencesUtil {
    /**
     * 重载非运算
     * */
    operator fun String?.not(): Boolean {
        return when (this) {
            null, "", "false" -> true
            "true" -> false
            else -> false
        }
    }

    fun putString(context: Context, key: String, value: String) {
        context.contentResolver.insert(CacheProvider.URI, ContentValues().apply {
            put(KEY, key)
            put(VALUE, value)
        })
    }

    fun getString(context: Context, key: String): String? {
        val c = context.contentResolver.query(CacheProvider.URI, null, key, null, null)
        return c?.let {
            it.moveToFirst()
            val v = it.getString(1)
            it.close()
            v
        }
    }

    fun removeString(context: Context, key: String) {
        context.contentResolver.delete(CacheProvider.URI, key, null)
    }
}