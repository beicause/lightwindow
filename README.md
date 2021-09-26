# 简介  

![输入图片说明](https://images.gitee.com/uploads/images/2021/0926/194412_d5589465_8444007.png "绘图111o.png")

窗隙流光，轻量迅捷的多功能悬浮窗应用。包括Android项目和Vue项目，摒弃传统的Activity界面构建方式，
基于WindowManager和WebView构建悬浮窗服务，通过JavaScriptInterface实现Android原生功能和前端Vue项目通信。子功能项目：

- 主界面[lightwindow_main](https://gitee.com/beicause/lightwindow_main)  
- 日程表[lightwindow_calendar](https://gitee.com/beicause/lightwindow_calendar)

## 运行要求

Android系统，最低Android 8.0（API Level 26）

## 下载

下载链接：<https://qingcheng.asia/app-release.apk>  
网页预览 <https://qingcheng.asia/main>（部分功能不支持）

## 特性

- 便捷：悬浮窗服务启动迅速，风格简约，操作方便，随时随地快速查看
- 高效：通过首次离线缓存无网络也可快速启动，后台占用极低
- 灵活：功能模块化实现，可移植易拓展，且作为网页混合应用易于更新

## 功能

目前已实现以下功能：
> 日程表：日程融入悬浮窗&通知栏  
项目仓库[lightwindow_calendar](https://gitee.com/beicause/lightwindow_calendar)，
网页预览 <https://qingcheng.asia/calendar>（部分功能不支持）

- 通过通知栏和悬浮窗展示日程
- 便捷添加，编辑，标记，设置提醒
- 目前支持导入下列大学的教务课表：
  - 合肥工业大学
  - 长沙理工大学
  - 赣南师范大学

---
>音乐谱：以字符构建乐谱  
项目仓库[lightwindow_main](https://gitee.com/beicause/lightwindow_main)，
网页预览 <https://qingcheng.asia/music>

## 项目结构

| lightwindow||
| ----  | ----  |
|\|--app  | 项目入口，连接主界面[lightwindow_main](https://gitee.com/beicause/lightwindow_main) |
|\|--base  | 其他模块依赖的一些工具类和基类 |
|\|--calendar|运行通知服务，连接日程表[lightwindow_calendar](https://gitee.com/beicause/lightwindow_calendar)|

| lightwindow_main||
| ----  | ----  |
|/  | 主页 |
|/main | 功能和说明页面 |
|/calendar|日程表页面|
|/music| 音乐谱页面|

## 参与贡献

本项目出于兴趣爱好、学习交流、方便使用而开发，完全开源、免费。对于您的任何贡献，我们非常感激。  
下面是我们努力的方向：  

- 优化代码结构、逻辑，解决存在的bug，提高性能和体验
- 日程表提供更多学校支持
- 开发更多新功能

如果您有任何想法或建议，或者做出任何改进，欢迎联系开发者。  
QQ：1494181792，邮箱：1494181792@qq.com  
