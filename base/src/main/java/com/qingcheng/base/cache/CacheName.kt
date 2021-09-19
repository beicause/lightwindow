package com.qingcheng.base.cache

/**
 * 枚举所有缓存，keyName也是缓存的键值
 * */
enum class CacheName {
    IS_FIRST,

    //这个缓存被main和cld写入
    WEB_VERSION,
    IGNORE_VERSION,
    MAIN_WIDTH,
    MAIN_HEIGHT,
}