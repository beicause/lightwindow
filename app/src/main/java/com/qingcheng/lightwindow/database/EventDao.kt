package com.qingcheng.lightwindow.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvents(event: List<Event>)

    @Delete
    suspend fun removeEvents(event: List<Event>)

    @Query("select day,time,title,detail,alarm,color from event ")
    fun getEvents(): Flow<List<Event>>

    @Query("select day,time,title,detail,alarm,color from event where alarm like '*%'")
    fun getAlarmEvents():Flow<List<Event>>
}