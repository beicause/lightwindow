package com.qingcheng.calllog

import android.util.Log
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val res=CallLogUtil.getLog(appContext).toString()
        var last=0
        for (i in res.indices step 300){
            Log.d("ttt",res.substring(last,i))
            last=i
        }
        Log.d("ttt",res.substring(last))
    }
}