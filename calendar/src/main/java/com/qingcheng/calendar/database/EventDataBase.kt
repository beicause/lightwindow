package com.qingcheng.calendar.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Event::class],version = 1)
abstract class EventDataBase :RoomDatabase(){
    companion object {
        const val DATABASE_NAME = "events"
    }

    abstract fun eventDao(): EventDao
}