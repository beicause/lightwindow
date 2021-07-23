package com.qingcheng.floatwindow.view

import android.content.Context
import kotlin.reflect.KClass

object ViewManager {
    val allView = mutableMapOf<String, Any>()

    inline fun <reified T> new(
        constructor: (Context) -> T,
        context: Context
    ): T {
        if (!allView.containsKey(T::class.simpleName)) {
            allView[T::class.simpleName!!] = constructor(context)!!
        }
        return allView[T::class.simpleName!!] as T
    }

    fun  get( clazz: KClass<out BaseFloatWindow<*>>): BaseFloatWindow<*> {
        return  allView[clazz.simpleName] as BaseFloatWindow<*>
    }

    fun get(vararg clazz: KClass<out BaseFloatWindow<*>>):Array<BaseFloatWindow<*>>{
        return Array(clazz.size){ allView[clazz[it].simpleName] as BaseFloatWindow<*>}
    }
    fun destroy(vararg clazz: KClass<out BaseFloatWindow<*>>) {
        clazz.forEach {
            (allView[it.simpleName] as BaseFloatWindow<*>).removeFromWindow()
            allView.remove(it.simpleName)
        }
    }

    fun destroyAll() {
        allView.forEach{ (_, v) ->(v as BaseFloatWindow<*>).removeFromWindow()}
        allView.clear()
    }
}