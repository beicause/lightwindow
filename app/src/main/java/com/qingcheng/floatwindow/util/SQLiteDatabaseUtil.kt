package com.qingcheng.floatwindow.util

import android.database.sqlite.SQLiteDatabase
import android.os.Build

object SQLiteDatabaseUtil {
    fun getSQLiteDatabase(path: String): SQLiteDatabase? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            SQLiteDatabase.openOrCreateDatabase(path, null)
        } else null
    }
}