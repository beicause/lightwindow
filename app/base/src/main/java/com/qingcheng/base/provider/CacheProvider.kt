package com.qingcheng.base.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.qingcheng.base.PACKAGE_NAME
import com.qingcheng.base.provider.CacheDataBase.Companion.DATABASE_NAME
import com.qingcheng.base.provider.CacheDataBase.Companion.KEY
import com.qingcheng.base.provider.CacheDataBase.Companion.VALUE


class CacheProvider : ContentProvider() {
    companion object {
        const val Auth = "$PACKAGE_NAME.cache.provider"
        const val URI_STR = "content://$Auth/$DATABASE_NAME"

        @JvmStatic
        val URI: Uri = Uri.parse(URI_STR)
        const val CACHE_KEY = KEY
        const val CACHE_VALUE = VALUE
    }

    private val matchRoot = 1
    private val matchRow = 2
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(Auth, DATABASE_NAME, matchRoot)
        addURI(Auth, "$DATABASE_NAME/#", matchRow)
    }
    private val verify = { values: ContentValues? ->
        if (values!!.size() != 2) throw Exception()
        var key = ""
        var value = ""
        values.keySet().forEach {
            if (values[it] !is String) throw Exception("value type must be String")
            when (it) {
                CACHE_KEY -> key = values[it] as String
                CACHE_VALUE -> value = values[it] as String
                else -> throw Exception()
            }
        }
        Cache(key, value)
    }
    private lateinit var dataBase: CacheDataBase
    private lateinit var dao: CacheDao

    fun set(dataBase: CacheDataBase) {
        this.dataBase = dataBase
        this.dao = dataBase.cacheDao()
    }

    override fun onCreate(): Boolean {
        dataBase = Room.databaseBuilder(context!!, CacheDataBase::class.java, DATABASE_NAME)
            .allowMainThreadQueries().build()
        dao = dataBase.cacheDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        var c: Cursor? = null
        if (uriMatcher.match(uri) == matchRow) c =
            dao.query(uri.lastPathSegment!!)?.toCursor(projection)
        if (uriMatcher.match(uri) == matchRoot) c = dao.query(selection!!)?.toCursor(projection)
        return c
    }

    override fun getType(uri: Uri): String {
        return "text/plain"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val kv = verify(values)
        dao.insert(kv)
        return Uri.parse(uri.toString() + "/${kv.key}")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return dao.delete(
            dao.query(selection!!) ?: throw Exception("attempt to delete a key that does not exist")
        )
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val kv = verify(values)
        return dao.update(kv)
    }
}