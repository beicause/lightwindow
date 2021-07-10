package com.qingcheng.floatwindow.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.widget.Toast

object SmileSchoolTokenUtil {
    fun initToken(context: Context) {
        val path =
            "/storage/emulated/0/Android/data/smileschool.newxstudio.com/apps/__UNI__147A402/doc/os.db"
        val database: SQLiteDatabase? = SQLiteDatabaseUtil.getSQLiteDatabase(path/*file.absolutePath*/)
        if (database != null) {
            val cursor = database.rawQuery("select value from user where \"key\" = \"token\"", null)
            cursor.moveToFirst()
            if (cursor.getString(0) != null && cursor.getString(0) != "") {
                SharedPreferencesUtil.put(
                    context,
                    "token",
                    cursor.getString(0)
                )
                cursor.close()
                database.close()
                return
            }
        }
        Handler(context.mainLooper).post {
            Toast.makeText(
                context,
                "请先在一点到校软件内登陆",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}