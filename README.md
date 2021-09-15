# 轻程悬浮窗应用

## 简介  

轻程Android应用，是基于WindowManager和WebView构建的便捷多功能悬浮窗APP。  
主界面项目[qc_app_main](https://gitee.com/beicause/qc_app_main)  
日程表项目[qc_app_calendar](https://gitee.com/beicause/qc_app_calendar)

## 运行要求

Android系统，最低Android 8.0（API Level 26）

## 下载

暂未创建发行版，您可以自行编译。  
网页预览 <https://qingcheng.asia/#/guide/>（部分功能不支持）

[//]:下载链接：请点击[这里](https://qingcheng.asia/app-release.apk)

## 特性

- 便捷：风格简约，操作方便，随时随地快速查看
- 高效：尽可能减少常驻后台，通过webview离线缓存达到快速启动，运行于独立进程防止内存泄漏
- 灵活：功能以Android library模块化实现，可移植易拓展，你甚至可以直接打包移植到其他应用上

## 功能

目前已实现以下功能：
> 日程表：随时打开，编辑事件，导入课表，添加提醒。  
项目仓库[qc_app_calendar](https://gitee.com/beicause/qc_app_calendar)，
网页预览 <https://qingcheng.asia/cld/>（部分功能不支持）

- 以日，周，月为单位，日程一览无余
- 快捷添加，编辑日程，打上标记
- 在轻程APP上支持通知和闹钟提醒
- 目前支持导入下列大学的教务课表：
  - 合肥工业大学
  - 长沙理工大学

---
>音乐谱  
项目仓库[qc_app_main](https://gitee.com/beicause/qc_app_main)，
网页预览 <https://qingcheng.asia/#/music/>

## 项目结构

| qingchengapp||
| ----  | ----  |
|\|--app  | 项目入口，连接主界面[qc_app_main](https://gitee.com/beicause/qc_app_main) |
|\|--base  | 其他模块依赖的一些工具类和基类 |
|\|--calendar|运行通知服务，连接日程表[qc_app_calendar](https://gitee.com/beicause/qc_app_calendar)|

| qc_app_main||
| ----  | ----  |
|/  | 主页 |
|/guide | 功能和说明页面 |
|/cld|日程表页面|
|/music| 音乐谱页面|

## 参与贡献

本项目注释详细，如果您有任何想法或改进，欢迎联系开发者和提交 pull request。  
QQ：1494181792，邮箱：1494181792@qq.com  
