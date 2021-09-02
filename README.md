# 轻程悬浮窗应用

## 简介  

轻程Android应用，是基于WindowManager和WebView构建的便捷多功能悬浮窗APP。  
子项目：[日程表qc_app_calendar](https://gitee.com/beicause/qc_app_calendar)

## 运行要求

Android系统，最低Android 8.0（API Level 26）

## 下载

下载链接：请点击[这里](https://qingcheng.asia/app-release.apk)

## 特性

- 便捷：风格简约，操作方便，随时随地快速查看
- 高效：尽可能减少常驻后台，通过webview离线缓存达到快速启动，运行于独立进程防止内存泄漏
- 灵活：功能以Android library模块化实现，可移植易拓展，你甚至可以直接打包移植到其他应用上

## 功能

目前已实现以下功能：
> 日程表：随时打开，编辑事件，导入课表，添加提醒。
参见项目仓库[qc_app_calendar](https://gitee.com/beicause/qc_app_calendar)

## 技术栈

> 本项目：Android  
编程语言：Kotlin  
其他库：数据库Room和Kotlin协程支持
---
> 日程表：框架Vue（uni-app）  
编程语言：Typescript  
其他库：vuex，axios，uni-ui

## 项目结构

| --

## 参与贡献

本项目注释详细，如果您有任何想法或改进，欢迎联系开发者和提交 pull request。  
QQ：1494181792，邮箱：1494181792@qq.com  
