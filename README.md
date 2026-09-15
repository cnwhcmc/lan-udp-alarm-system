# LAN UDP Alarm System — Find My Device 📡

> 局域网 UDP 警报系统（找设备）
> Trigger a max-volume alarm + vibration on a lost Android phone from an old smartwatch over the same Wi-Fi LAN.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/)

---

## English

**Lost your phone somewhere in the house?** This project lets an old Android smartwatch (Android 7.1.1) trigger a **max-volume alarm + continuous vibration** on a target Android phone (Android 10+) — as long as both devices are connected to the **same Wi-Fi router**.

### Features

- 🔔 **Max-volume alarm** — auto-raises media volume to maximum, loops an alert sound
- 📳 **Continuous vibration** — vibrate 1s / pause 0.5s, repeating until stopped
- 📡 **LAN UDP control** — fixed port `8888`, works only inside the same Wi-Fi LAN
- 🛡️ **Foreground Service** — persistent notification keeps the listener alive (required on modern Android)
- 📶 **Wi-Fi loss detection** — auto-opens the system Wi-Fi settings page if the network drops (no root, no auto-connect)

### How it works

```
┌─────────────────┐        UDP 8888        ┌─────────────────┐
│   Watch side    │ ─────────────────────► │   Phone side    │
│  Android 7.1.1  │   ALARM_ON / ALARM_OFF │  Android 10+    │
│  sends command  │ ◄───────────────────── │  receives &     │
│                 │                        │  rings & vibrates│
└─────────────────┘                        └─────────────────┘
```

### Command protocol

| Command | Effect |
|---------|--------|
| `ALARM_ON`  | Max-volume alarm + continuous vibration |
| `ALARM_OFF` | Stop alarm & vibration, restore original volume |

### Quick start

1. **Phone (receiver, Android 10+)** — connect to Wi-Fi, install `apk/PhoneReceiver.apk`, open the app. A persistent notification appears. Note the phone's LAN IP (Settings → WLAN → tap connected network).
2. **Watch (controller, Android 7.1.1)** — connect to the **same** router, install `apk/WatchController.apk`, enter the phone's IP.
3. Tap **开启警报 (ALARM_ON)** to ring, **关闭警报 (ALARM_OFF)** to stop.

> ⚠️ Requires the alert audio file: place `alert.wav` into `receiver/res/raw/alert.wav` before building.

### Known issues (real-world pitfalls)

| Issue | Symptom | Fix |
|-------|---------|-----|
| Router AP isolation | Watch & phone can't reach each other | Disable **AP isolation** in the router admin panel |
| System kills foreground service | No response after long sleep | Reopen the app; enable *Auto-start / Background / Unrestricted* battery settings for the app |
| Wi-Fi disconnected | No communication | App auto-opens Wi-Fi settings — re-enable Wi-Fi manually |
| Firewall / security app | UDP 8888 packets dropped | Whitelist the app or disable the firewall |

Debug tip: test the UDP link first from a PC — `nc -u -l 8888` on the listener, then `echo "ALARM_ON" | nc -u <IP> 8888` from the sender.

### Project structure

```
lan-udp-alarm-system/
├── README.md                       # this file
├── CHANGELOG.md                    # release history
├── LICENSE                         # MIT license
├── apk/                            # prebuilt installers
│   ├── PhoneReceiver.apk           # receiver (phone, Android 10+)
│   └── WatchController.apk         # controller (watch, Android 7.1.1)
├── receiver/                       # phone side source
│   ├── UdpListenService.java       # UDP foreground service
│   ├── AndroidManifest.xml
│   └── res/raw/                    # place alert.wav here
├── controller/                     # watch side source
│   ├── MainActivity.java           # UI + UDP sender
│   ├── res/layout/activity_main.xml
│   └── AndroidManifest.xml
└── docs/
    ├── usage-troubleshooting.md    # step-by-step guide & troubleshooting
    └── 局域网UDP警报系统：WiFi检测与控制实现.pdf  # original design doc
```

### Build from source

The `receiver` and `controller` folders contain the complete Java source and manifests. Create a standard Android project (Gradle), copy the sources into the matching package (`com.test.receiver` / `com.test.controller`), add `alert.wav` to `receiver/res/raw/`, and build. Min SDK: 7.1.1 (API 25) for the controller; target Android 10+ (API 29+) for the receiver.

### License

[MIT](LICENSE) © 2026

---

## 中文说明

**这是什么？** 用一台老安卓手表（Android 7.1.1）控制同一 WiFi 局域网内的手机（Android 10+）发出**最大音量警报 + 持续振动**，用于快速找到乱放/丢失的手机。

**核心约定：**
- 端口固定 `8888`，同一 WiFi 局域网内生效，外网无效
- 被控手机 WiFi 需**手动**提前打开；APP 只检测 WiFi 断开并跳转系统设置页，不做自动开 WiFi
- 前台服务通知**不能去掉**，否则高版本安卓会直接杀死 Service
- 构建前需将警报音频 `alert.wav` 放入 `receiver/res/raw/alert.wav`

**使用方法：** 手机装 `apk/PhoneReceiver.apk`，手表装 `apk/WatchController.apk`，两端连同一路由器，手表输入手机局域网 IP，点【开启警报】即可。详细步骤与故障排查见 [docs/usage-troubleshooting.md](docs/usage-troubleshooting.md)（含 AP 隔离、杀进程、防火墙等已知问题）。

**注**：此软件使用了**AI编写**，但是**保证稳定性**，中国人不骗中国人，来都来了，试试嘛
