package com.qingcheng.base.view

import kotlin.reflect.KClass

/**
 * 悬浮窗管理类
 * */
object ViewManager {
    val allView = mutableMapOf<String, Any>()

    /**
     * 创建悬浮窗实例，不会重复创建
     * @return 悬浮窗实例
     * */
    inline fun <reified T> new(view: BaseFloatWindow<*>,name:String?=null): T {
        val n = name?:T::class.simpleName!!
        if (!allView.containsKey(n)) {
            allView[n] = view
        }
        return allView[n] as T
    }

    /**
     * 获取悬浮窗实例
     * @param clazz 悬浮窗类
     * @return 悬浮窗实例，若不存在则为null
     * */
    fun  get( clazz: KClass<out BaseFloatWindow<*>>): BaseFloatWindow<*>? {
        return  allView[clazz.simpleName] as BaseFloatWindow<*>?
    }

    fun get(name:String): BaseFloatWindow<*>? {
        return allView[name] as BaseFloatWindow<*>?
    }
    /**
     * 删除悬浮窗实例
     * @param clazz 悬浮窗类
     * */
    fun destroy(vararg clazz: KClass<out BaseFloatWindow<*>>) {
        clazz.forEach {
            (allView[it.simpleName] as BaseFloatWindow<*>).removeFromWindow()
            allView.remove(it.simpleName)
        }
    }

    fun destroy(vararg name: String){
        name.forEach {
            (allView[it] as BaseFloatWindow<*>).removeFromWindow()
            allView.remove(it)
        }
    }

    /**
     * 删除所有悬浮窗实例
     * */
    fun destroyAll() {
        allView.forEach{ (_, v) ->(v as BaseFloatWindow<*>).removeFromWindow()}
        allView.clear()
    }
}
