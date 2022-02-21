//package com.qingcheng.base.util
//
//import android.content.Context
//import android.content.SharedPreferences
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.*
//import androidx.datastore.preferences.preferencesDataStore
//import com.qingcheng.base.SP_CACHE_NAME
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.runBlocking
//
//
//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SP_CACHE_NAME)
//
///**
// * SharedPreferences工具
// * @see SharedPreferences
// * */
//object SharedPreferencesUtil {
//    //默认的sp文件名
//    private const val defaultName: String = SP_CACHE_NAME
//
//    /**
//     * sharedPreference存入键值对
//     * @param context
//     * @param key 键
//     * @param value 值
//     * */
//    suspend fun put(context: Context, key: String, value: String) {
//        context.dataStore.edit { it[stringPreferencesKey(key)] = value }
//    }
//
//    suspend fun put(context: Context, key: String, value: Int) {
//        context.dataStore.edit { it[intPreferencesKey(key)] = value }
//    }
//
//    suspend fun put(context: Context, key: String, value: Boolean) {
//        context.dataStore.edit { it[booleanPreferencesKey(key)] = value }
//    }
//
//    fun putSync(context: Context, key: String, value: String) {
//        runBlocking { put(context, key, value) }
//    }
//
//    fun putSync(context: Context, key: String, value: Int) {
//        runBlocking { put(context, key, value) }
//    }
//
//    fun putSync(context: Context, key: String, value: Boolean) {
//        runBlocking { put(context, key, value) }
//    }
//
//    /**
//     * 默认值为 “null”字符
//     * */
//    suspend fun getString(context: Context, key: String): String {
//        return context.dataStore.data.map { it[stringPreferencesKey(key)] }.first() ?: "null"
//    }
//
//    suspend fun getInt(context: Context, key: String): Int =
//        context.dataStore.data.map { it[intPreferencesKey(key)] }.first() ?: 0
//
//    suspend fun getBoolean(context: Context, key: String): Boolean =
//        context.dataStore.data.map { it[booleanPreferencesKey(key)] }.first() ?: false
//
//    fun getStringSync(context: Context, key: String): String =
//        runBlocking { getString(context, key) }
//
//    fun getIntSync(context: Context, key: String): Int = runBlocking { getInt(context, key) }
//
//    fun getBooleanSync(context: Context, key: String): Boolean = runBlocking {
//        getBoolean(context, key)
//    }
//}