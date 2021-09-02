package com.qingcheng.baseutil.util

import java.io.File

/**
 * 文件操作
 * */
object FileUtil {
    /**
     * 删除文件或文件夹的所有文件
     * @param file 文件或文件夹
     * */
    fun deleteDir(file: File):Boolean {
        if (!file.exists()) return false
        if (file.isFile) return file.delete()
        if (file.isDirectory) {
            if (file.listFiles()==null)return file.delete()
            else file.listFiles()?.let {
                if (it.isEmpty()) return file.delete()
                var res=true
                for (f in it) {
                    val r=deleteDir(f)
                    if (!r)res=false
                }
                return res
            }
        }
        return false
    }
//    fun getFilesSize(file: File):Long{
//        if (!file.exists())return -1
//        if (file.isFile)return file.length()
//        if (file.isDirectory){
//            if (file.listFiles()==null)return -1
//            else file.listFiles()?.let {
//                if (it.isEmpty())return 0
//                var s=0L
//                for (f in it)s+= getFilesSize(f)
//                return s
//            }
//        }
//        return -1
//    }
}