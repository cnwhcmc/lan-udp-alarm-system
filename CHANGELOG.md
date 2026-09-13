# Changelog

本项目所有重要变更都会记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-09-13

### Added

- 初始发布：局域网 UDP 警报系统（找设备）完整源码与文档
- 被控接收端（手机，Android 10+）：`UdpListenService` 前台服务
  - UDP 8888 端口监听 `ALARM_ON` / `ALARM_OFF` 指令
  - 最大音量循环播放警报音 + 持续循环振动（振动 1s / 停 0.5s）
  - 停止警报后恢复原媒体音量
  - WiFi 断开检测：自动跳转系统 WiFi 设置页（不做自动开 WiFi、无 root 代码）
- 手表控制端（Android 7.1.1）：`MainActivity` 输入 IP 发送 UDP 指令
- 预编译安装包：`apk/PhoneReceiver.apk`、`apk/WatchController.apk`
- 文档：`README.md`（中英双语）、`docs/usage-troubleshooting.md`、原始设计 PDF
