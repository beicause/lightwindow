package com.qingcheng.lightwindow.cache

/**
 * 枚举所有缓存，keyName也是缓存的键值
 * */
enum class CacheName( val keyName:String) {
//      CACHE_IS_FIRST("is_first") ,
      CACHE_MAIN_VERSION("main_version"),
      CACHE_CLD_VERSION("cld_version"),
      CACHE_MAIN_WIDTH("main_width"),
      CACHE_MAIN_HEIGHT("main_height"),
}