package com.qingcheng.calllog

import android.content.Context
import android.provider.CallLog.Calls
import org.json.JSONArray
import org.json.JSONObject

object CallLogUtil {
    fun getLog(context: Context): JSONArray {
        val cursor = context.contentResolver.query(
            Calls.CONTENT_URI,
            arrayOf(Calls.CACHED_NAME, Calls.NUMBER, Calls.DATE, Calls.TYPE),
            null,
            null,
            Calls.DEFAULT_SORT_ORDER
        )!!
        val res = JSONArray()
        while (cursor.moveToNext()) {
            res.put(JSONObject().apply {
                put("name", cursor.getString(0))
                put("number", cursor.getString(1))
                put("date", cursor.getString(2))
                put("type", cursor.getString(3))
            })
        }
        cursor.close()
        return res
    }
}