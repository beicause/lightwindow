package com.qingcheng.base

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qingcheng.base.provider.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CacheDatabaseTest {
    private lateinit var dao: CacheDao
    private lateinit var db: CacheDataBase
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun createDb() {
        db = Room.databaseBuilder(
            context, CacheDataBase::class.java, DATABASE_NAME
        ).build()
        dao = db.cacheDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndRead() {
        val cache = Cache("k", "v")
        dao.insert(cache)
        val c = dao.query(cache.key).toCursor()
        c.moveToFirst()
        assertEquals(c.columnCount, 2)
        assertEquals(c.getString(0), cache.key)
        assertEquals(c.getString(1), cache.value)
    }
}
