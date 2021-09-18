package com.qingcheng.base.cache

/**
 * 枚举所有缓存，keyName也是缓存的键值
 * */
enum class CacheName {
      CACHE_IS_FIRST,

      //这个缓存被main和cld写入
      CACHE_VERSION,
      CACHE_MAIN_WIDTH,
      CACHE_MAIN_HEIGHT,
}