# 任务记录：局域网 UDP 警报系统（找设备）

## 任务来源

本次任务来源于以下三个链接，属于同一项目的完整资料：

| 序号 | 链接 | 类型 | 访问状态 |
|------|------|------|----------|
| 1 | https://www.doubao.com/thread/xFTcTjfli5ktwA7C7 | 豆包对话线程 | ✅ 已成功读取并整理 |
| 2 | https://feishu.doubao.com/docx/TikDdsSOpo62eixmXBScvLi5nLf | 飞书文档（docx） | ✅ 已通过用户上传附件获取（PDF） |
| 3 | https://feishu.doubao.com/drive/file/XkszbZlR7oXZ1Ox2EkzcLqGvnpd | 飞书云盘文件 | ✅ 已通过用户上传附件获取（APK压缩包） |

> **说明**：飞书链接 2 和 3 虽无直接访问权限，但用户已上传对应附件，内容已完整获取并整合到本项目中。

---

## 任务需求概述

有两个电子设备：
- **设备 A（控制端）**：安卓 7.1.1（手表）
- **设备 B（被控端）**：安卓十以上（手机）

需求：在相同 WiFi 的情况下，用安卓 7.1.1 设备控制安卓十以上的设备进行振动以及播放声音，主要是为了寻找这个电子设备。

---

## 已整理的项目内容

从豆包链接中已完整提取并保存以下代码和文档：

### 项目结构

```
lan-udp-alarm-system/
├── README.md                          # 项目总说明
├── TASK_RECORD.md                     # 本文件（任务记录）
├── apk/                               # 编译好的安装包
│   ├── PhoneReceiver.apk              # 被控接收端 APK（手机）
│   └── WatchController.apk            # 手表控制端 APK（安卓7.1.1）
├── receiver/                          # 被控接收端（手机，Android 10+）
│   ├── UdpListenService.java          # UDP 监听前台服务
│   ├── AndroidManifest.xml            # 权限与服务声明
│   └── res/raw/                       # 放置 alert.wav 警报音频
├── controller/                        # 手表控制端（Android 7.1.1）
│   ├── MainActivity.java              # 主界面，发送 UDP 指令
│   ├── res/layout/activity_main.xml   # 布局文件
│   └── AndroidManifest.xml            # 权限声明
└── docs/
    ├── usage-troubleshooting.md       # 使用步骤 + 故障排查
    └── 局域网UDP警报系统：WiFi检测与控制实现.pdf  # 原始设计文档
```

### 核心技术方案

- **通信方式**：同一 WiFi 局域网内 UDP 通信
- **固定端口**：8888（外网无效）
- **指令协议**：`ALARM_ON`（开启警报）/ `ALARM_OFF`（停止警报）
- **警报效果**：被控手机最大音量播放警报音 + 持续循环振动
- **保活机制**：前台 Service + 常驻通知（不可去掉，否则高版本安卓杀进程）
- **WiFi 检测**：APP 只检测 WiFi 是否断开，断开则跳转系统 WiFi 设置页，不自动开 WiFi

### 关键约束

1. 被控手机 WiFi 需手动提前打开，保持连接同一局域网路由器
2. 不做 root 相关代码
3. 前台服务通知不能去掉
4. 音频资源 `alert.wav` 需放到 `res/raw/` 目录

---

## 附件获取内容（已整合）

用户后续上传了两个附件，对应飞书链接 2 和 3 的内容：

1. **局域网UDP警报系统：WiFi检测与控制实现.pdf**
   - 对应飞书文档链接，包含完整的设计说明、代码片段、使用步骤和故障点
   - 已保存至 `docs/` 目录
   - 内容与豆包链接整理的代码一致，已据此修正布局文件中的按钮文字

2. **局域网下震铃控制软件.zip**
   - 对应飞书云盘文件链接，包含两个编译好的 APK 安装包
   - `PhoneReceiver.apk`（145KB）— 被控接收端，安装到手机
   - `WatchController.apk`（18KB）— 手表控制端，安装到安卓7.1.1手表
   - 已保存至 `apk/` 目录

---

## 已知故障点（已整理）

1. **路由器 AP 隔离**：手表与手机互相收不到 UDP 包，需关闭路由器 AP 隔离
2. **系统杀前台 Service**：被控手机休眠后系统可能杀掉 Service，需重启 APP；root 可调 `oom_score_adj` 缓解
3. **被控 WiFi 断开**：APP 自动跳 WiFi 设置页，需手动点开 WiFi
4. **防火墙/安全软件拦截**：UDP 8888 端口被直接丢包，需关闭防火墙或加白名单

---

## 记录时间

- 创建时间：2026-09-09
- 最后更新：2026-09-09
