package com.qingcheng.lightwindow.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    /**
     * 添加一组event
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvents(event: List<Event>)
    /**
     * 删除一组event
     * */
    @Delete
    suspend fun removeEvents(event: List<Event>)

    /**
     * 获取所有event
     * */
    @Query("select day,time,title,detail,alarm,color from event ")
    fun getEvents(): Flow<List<Event>>

    /**
     * 获取所有闹钟事件
     * */
    @Query("select day,time,title,detail,alarm,color from event where alarm like '*%'")
    fun getAlarmEvents():Flow<List<Event>>
}