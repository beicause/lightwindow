# 简介  

![band](https://gitee.com/beicause/lightwindow/raw/master/band.png)

窗隙流光，轻量迅捷的多功能悬浮窗应用。 
~~web项目：[lightwindow_webapp](https://github.com/beicause/lightwindow_webapp)~~ web项目已合并至本仓库

## 运行要求

Android系统，最低Android 8.0（API Level 26）

## 下载

网页链接：<https://qingcheng.asia>

## 特性

- 便捷：悬浮窗服务启动迅速，风格简约，操作方便，随时随地快速查看
- 高效：通过首次离线缓存无网络也可快速启动，后台占用极低
- 灵活：功能模块化实现，可移植易拓展，且作为网页混合应用易于更新

## 功能

目前已实现以下功能：
> 日程表：通知浮窗，便捷查看，支持导入教务课表  

- 通过通知栏和悬浮窗展示日程
- 便捷添加，编辑，标记，设置提醒
- 目前支持导入下列大学的教务课表：
  - 合肥工业大学
  - 长沙理工大学
  - 赣南师范大学

---
>音乐谱：以字符构建乐谱  

## 项目结构

| lightwindow||
| ----  | ----  |
|\|--app  | 项目入口，连接主界面 |
|\|--base  | 其他模块依赖的一些工具类和基类 |
|\|--calendar| 运行通知服务，连接日程表 |

| lightwindow_webapp||
| ----  | ----  |
|/  | 主页 |
|/main | 功能和说明页面 |
|/calendar|日程表页面|
|/music| 音乐谱页面|

## 参与贡献

联系开发者。  
QQ：1494181792，邮箱：1494181792@qq.com  
