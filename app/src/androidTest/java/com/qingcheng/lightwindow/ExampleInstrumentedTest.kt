package com.qingcheng.lightwindow

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qingcheng.calendar.util.gnnu.GnnuRequest
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            println(
                GnnuRequest.getGnnuSchedule("", "")
            )
        }


        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        val dataBase = Room.inMemoryDatabaseBuilder(
//            appContext, EventDataBase::class.java
//        ).build()
//        Log.i("开始","---")
//        MainScope().launch {
//            withContext(Dispatchers.IO) {
//                dataBase.eventDao().addEvents(listOf(Event(alarm = "*40"),Event()))
//                dataBase.eventDao().getEvents().first().let {
//                    Log.i("所有",it.toString())
//                }
//            }
//            withContext(Dispatchers.IO){
//                dataBase.eventDao().getAlarmEvents().collect {
//                    Log.i("闹钟",it.toString())
//                }
//            }
//        }
    }
}