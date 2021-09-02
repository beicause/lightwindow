package com.qingcheng.calender.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Event::class],version = 1)
abstract class EventDataBase :RoomDatabase(){
    abstract fun eventDao():EventDao
}