//package com.qingcheng.base;
//
//import static com.qingcheng.base.provider.CacheDataBaseKt.VALUE;
//import static com.qingcheng.base.provider.CacheDataBaseKt.KEY;
//import static org.junit.Assert.assertEquals;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//
//import com.qingcheng.base.provider.CacheProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.rule.provider.ProviderTestRule;
//
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class ProviderTest {
//    @Rule
//    public ProviderTestRule provider = new ProviderTestRule.Builder(CacheProvider.class, CacheProvider.Auth).build();
//
//    @Test
//    public void writeAndReadProvider() {
//        ContentValues values=new ContentValues();
//        values.put(KEY,"k");
//        values.put(VALUE,"v");
//        provider.getResolver().insert(CacheProvider.getURI(),values );
//        Cursor c = provider.getResolver().query(CacheProvider.getURI(), null, "k", null, null);
//        c.moveToFirst();
//        assertEquals(c.getString(0), "k");
//    }
//}
