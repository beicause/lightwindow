package com.qingcheng.floatwindow.util

import android.content.Context

object SharedPreferencesUtil {
        private const val SP_NAME = "cache"
        fun put(context: Context, key: String, value: String) {
            val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            sp.edit().putString(key, value).apply()
        }

        fun getString(context: Context, key: String): String {
            return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(key, "")!!
        }

        fun put(context: Context, key: String, value: Int) {
            val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            sp.edit().putInt(key, value).apply()
        }

        fun getInt(context: Context, key: String): Int {
            return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(key, 0)
        }

}