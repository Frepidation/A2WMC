# A2WMC — Android to Windows Media Controller

Bridge Android 媒体播放状态到 Windows SMTC（System Media Transport Controls）。

## 功能

- **Android → Windows 同步**：手机播放的音乐、视频信息实时同步到 Windows 系统媒体控件
- **SMTC 集成**：Windows 任务栏可直接显示专辑封面、曲目名称、进度条并控制播放
- **ADB 连接**：通过 Wi-Fi/ADB 连接 Android 设备
- **TCP 协议**：自定义协议传输轨道元数据、封面和播放状态

## 工作原理

```
┌─────────────┐     TCP      ┌──────────────┐
│   Android    │────────────→│   Windows     │
│  (监听通知)   │   Metadata  │  (SMTC 服务)  │
└─────────────┘             └──────────────┘
```

Android 端监听媒体通知，提取曲目信息、封面、进度，通过 TCP 发送给 Windows 端。Windows 端更新 SMTC，使任务栏媒体控件实时反映手机播放状态。

## 项目结构

```
3cord/
├── android/          # Android 源码 (Java)
│   └── app/
│       ├── build.gradle
│       ├── src/main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/frepidation/a2wmc/
│       │   │   ├── MainActivity.java
│       │   │   ├── model/TrackInfo.java
│       │   │   ├── service/BridgeService.java      — 核心桥接服务
│       │   │   ├── service/NotifListenerService.java — 通知监听
│       │   │   ├── transport/ControlChannel.java     — TCP 控制通道
│       │   │   └── transport/MetadataProtocol.java   — 元数据协议
│       │   └── res/
│       └── ...
└── windows-src/      # Windows 源码 (C# .NET 8)
    └── A2WMC.Windows/
        ├── MainForm.cs
        ├── Program.cs
        ├── Models/TrackInfo.cs
        └── Services/
            ├── AdbHelper.cs           — ADB 连接管理
            ├── CoverHelper.cs         — 唱片封面处理
            ├── ShutdownHelper.cs      — 关机检测
            ├── SmtcService.cs         — SMTC 集成
            ├── TcpListenerService.cs  — TCP 服务端
            └── VolumeSynchronizer.cs  — 音量同步
```

## 构建

### Android
用 Android Studio 打开 `android/` 目录，直接 Build 即可。

### Windows
```
dotnet build windows-src/A2WMC.Windows/
```

需要 .NET 8 SDK
需要 scrcpy支持音频传输

## 版本

v0.1.0
