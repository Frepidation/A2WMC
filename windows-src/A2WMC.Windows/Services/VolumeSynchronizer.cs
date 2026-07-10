namespace A2WMC.Services;

/// <summary>
/// 音量同步 — 通过 Windows 系统音量变化事件同步到 Android。
/// </summary>
public class VolumeSynchronizer : IDisposable
{
    private bool _disposed;
    public void Start()
    {
        // 简化版：直接由 TCP 端触发，无需 Windows 端音量监听
        // 后续可扩展
    }

    public void SetVolume(float vol01)
    {
        // 可通过 NAudio 或 Win32 API 设置 Windows 音量
        // 当前版本仅做标记
    }

    public void Dispose()
    {
        if (_disposed) return;
        _disposed = true;
        GC.SuppressFinalize(this);
    }
}
