package com.qingcheng.base.provider

import android.database.Cursor
import android.database.MatrixCursor
import androidx.room.*
import com.qingcheng.base.provider.CacheDataBase.Companion.DATABASE_NAME
import com.qingcheng.base.provider.CacheDataBase.Companion.KEY
import com.qingcheng.base.provider.CacheDataBase.Companion.VALUE


@Entity
data class Cache(
    @PrimaryKey
    val key: String,
    val value: String,
)

fun Cache.toCursor(cols: Array<out String>? = arrayOf(KEY, VALUE)): Cursor {
    val mCols = cols ?: arrayOf(KEY, VALUE)
    return MatrixCursor(mCols).also {
        val r = mutableListOf<String>()
        if (mCols.contains(KEY)) r.add(this.key)
        if (mCols.contains(VALUE)) r.add(this.value)
        it.addRow(r)
    }
}

@Dao
interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cache: Cache): Long

    @Delete
    fun delete(cache: Cache): Int

    @Update
    fun update(cache: Cache): Int

    @Query("select `keY`,value from $DATABASE_NAME where `key` = :key")
    fun query(key: String): Cache?
}

@Database(entities = [Cache::class], version = 1)
abstract class CacheDataBase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "cache"
        const val KEY = "key"
        const val VALUE = "value"
    }

    abstract fun cacheDao(): CacheDao
}
