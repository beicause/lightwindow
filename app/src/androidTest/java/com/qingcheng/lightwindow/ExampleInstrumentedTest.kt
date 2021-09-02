package com.qingcheng.lightwindow

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qingcheng.calender.database.Event
import com.qingcheng.calender.database.EventDataBase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @InternalCoroutinesApi
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataBase = Room.inMemoryDatabaseBuilder(
            appContext, EventDataBase::class.java
        ).build()
        Log.i("开始","---")
        MainScope().launch {
            withContext(Dispatchers.IO) {
                dataBase.eventDao().addEvents(listOf(Event(alarm = "*40"),Event()))
                dataBase.eventDao().getEvents().first().let {
                    Log.i("所有",it.toString())
                }
            }
            withContext(Dispatchers.IO){
                dataBase.eventDao().getAlarmEvents().collect {
                    Log.i("闹钟",it.toString())
                }
            }
        }
    }
}