using System.Diagnostics;

namespace A2WMC.Services;

/// <summary>
/// ADB 相关操作：查找、设备列表、端口转发、scrcpy 隧道
/// </summary>
public class AdbHelper : IDisposable
{
    private readonly string _adbPath;
    private readonly string _scrcpyPath;
    private Process? _scrcpyProcess;
    private bool _disposed;

    public AdbHelper(string? customAdbPath = null)
    {
        _adbPath = FindAdb(customAdbPath);
        _scrcpyPath = FindScrcpy();
    }

    public string AdbPath => _adbPath;
    public string ScrcpyPath => _scrcpyPath;
    public bool AdbAvailable => !string.IsNullOrEmpty(_adbPath) && File.Exists(_adbPath);
    public bool ScrcpyAvailable => !string.IsNullOrEmpty(_scrcpyPath) && File.Exists(_scrcpyPath);

    /// <summary>扫描 ADB 设备列表</summary>
    public async Task<List<AdbDevice>> ListDevicesAsync()
    {
        var list = new List<AdbDevice>();
        if (!AdbAvailable) return list;

        var output = await RunAdbAsync("devices -l");
        if (string.IsNullOrEmpty(output)) return list;

        var lines = output.Split('\n', StringSplitOptions.RemoveEmptyEntries);
        foreach (var line in lines)
        {
            var trimmed = line.Trim();
            if (trimmed.StartsWith("List of devices")) continue;
            if (string.IsNullOrWhiteSpace(trimmed)) continue;

            // Format: "0123456789ABCDEF       device product:xxx model:xxx device:xxx"
            var parts = trimmed.Split([' ', '\t'], StringSplitOptions.RemoveEmptyEntries);
            if (parts.Length < 2) continue;
            if (parts[1] != "device") continue;

            var device = new AdbDevice
            {
                Serial = parts[0],
                Status = parts.Length > 1 ? parts[1] : "unknown"
            };
            // 解析 -l 信息
            for (int i = 2; i < parts.Length; i++)
            {
                if (parts[i].StartsWith("model:"))
                    device.Model = parts[i].Substring(6).TrimEnd(':');
                else if (parts[i].StartsWith("product:"))
                    device.Product = parts[i].Substring(8).TrimEnd(':');
            }
            list.Add(device);
        }
        return list;
    }

    /// <summary>建立 adb reverse 隧道: tcp:localPort ↔ tcp:remotePort</summary>
    public async Task<bool> ReverseTunnelAsync(string deviceSerial, int localPort, int remotePort)
    {
        if (!AdbAvailable) return false;

        // 先清除旧的
        await RunAdbAsync($"-s {deviceSerial} reverse --remove tcp:{localPort}");
        // 建立新的
        var result = await RunAdbAsync($"-s {deviceSerial} reverse tcp:{localPort} tcp:{remotePort}");
        if (!string.IsNullOrEmpty(result) && !result.Contains("error", StringComparison.OrdinalIgnoreCase))
        {
            // 可能只是 info 输出，不是错误
        }
        return string.IsNullOrEmpty(result) || !result.Contains("error", StringComparison.OrdinalIgnoreCase);
    }

    /// <summary>启动 scrcpy（ADB 模式专用）</summary>
    public bool StartScrcpy(string deviceSerial)
    {
        if (!ScrcpyAvailable || !AdbAvailable) return false;
        KillScrcpy();

        try
        {
            _scrcpyProcess = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = _scrcpyPath,
                    // 新版 scrcpy 用 --no-window；旧版用 -m 代替
                    // 不加 --no-window，用 -m 1024 --turn-screen-off 保持后台
                    Arguments = $"-s {deviceSerial} -m 1024 --turn-screen-off",
                    UseShellExecute = true,
                    WindowStyle = ProcessWindowStyle.Hidden,
                    CreateNoWindow = true,
                },
                EnableRaisingEvents = true
            };

            // 不把 _scrcpyProcess 设 null，用 _scrcpyExited 标记
            var exited = false;
            _scrcpyProcess.Exited += (_, _) =>
            {
                exited = true;
                Debug.WriteLine("[ADB] scrcpy exited");
            };

            var started = _scrcpyProcess.Start();
            if (started)
            {
                // 给 scrcpy 一点时间启动，如果立即退出可能是参数问题
                Thread.Sleep(500);
                if (_scrcpyProcess.HasExited)
                {
                    Debug.WriteLine("[ADB] scrcpy exited immediately");
                }
            }
            return started;
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[ADB] scrcpy start error: {ex.Message}");
            return false;
        }
    }

    public bool IsScrcpyRunning() => _scrcpyProcess != null && !_scrcpyProcess.HasExited;

    /// <summary>移除 ADB 反向隧道</summary>
    public async Task RemoveReverseTunnelAsync(string deviceSerial, int port)
    {
        if (!AdbAvailable) return;
        await RunAdbAsync($"-s {deviceSerial} reverse --remove tcp:{port}");
    }

    /// <summary>执行 ADB 命令</summary>
    public async Task<string> RunAdbAsync(string args)
    {
        if (!AdbAvailable) return "";

        try
        {
            var tcs = new TaskCompletionSource<string>();

            var psi = new ProcessStartInfo
            {
                FileName = _adbPath,
                Arguments = args,
                UseShellExecute = false,
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                CreateNoWindow = true,
                StandardOutputEncoding = System.Text.Encoding.UTF8,
                StandardErrorEncoding = System.Text.Encoding.UTF8,
            };

            using var proc = new Process { StartInfo = psi, EnableRaisingEvents = true };
            var output = new System.Text.StringBuilder();
            var error = new System.Text.StringBuilder();

            proc.OutputDataReceived += (_, e) => { if (e.Data != null) output.AppendLine(e.Data); };
            proc.ErrorDataReceived += (_, e) => { if (e.Data != null) error.AppendLine(e.Data); };
            proc.Exited += (_, _) => tcs.TrySetResult("");

            if (!proc.Start())
            {
                return "";
            }

            proc.BeginOutputReadLine();
            proc.BeginErrorReadLine();

            // 等待退出（最长 10s）
            await Task.WhenAny(tcs.Task, Task.Delay(10000));
            if (!proc.HasExited)
            {
                try { proc.Kill(entireProcessTree: true); } catch { }
            }

            var outText = output.ToString().Trim();
            var errText = error.ToString().Trim();

            return string.IsNullOrEmpty(errText) ? outText : $"{outText}\n{errText}".Trim();
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[ADB] Run error: {ex.Message}");
            return "";
        }
    }

    public void KillScrcpy()
    {
        if (_scrcpyProcess == null) return;
        try
        {
            if (!_scrcpyProcess.HasExited)
            {
                _scrcpyProcess.Kill(entireProcessTree: true);
                _scrcpyProcess.WaitForExit(3000);
            }
        }
        catch { }
        finally
        {
            try { _scrcpyProcess.Dispose(); } catch { }
            _scrcpyProcess = null;
        }
    }

    public void Dispose()
    {
        if (_disposed) return;
        _disposed = true;
        KillScrcpy();
        GC.SuppressFinalize(this);
    }

    // ==================== 路径查找 ====================
    // 部署结构: distN/A2WMC.exe + distN/scrcpy/adb.exe + distN/scrcpy/scrcpy.exe

    private static string FindAdb(string? customPath)
    {
        if (!string.IsNullOrEmpty(customPath) && File.Exists(customPath))
            return customPath;

        // 优先找 exe 同目录下的 scrcpy/adb.exe（标准部署结构）
        var baseDir = AppDomain.CurrentDomain.BaseDirectory;
        var candidates = new[]
        {
            Path.Combine(baseDir, "scrcpy", "adb.exe"),
            Path.Combine(baseDir, "adb.exe"),
            "adb.exe",  // PATH 或当前目录
        };

        // 备选：ANDROID_HOME 里的 platform-tools
        if (Environment.GetEnvironmentVariable("ANDROID_HOME") is { Length: > 0 } androidHome)
        {
            var sdkPath = Path.Combine(androidHome, "platform-tools", "adb.exe");
            if (File.Exists(sdkPath)) return sdkPath;
        }

        foreach (var c in candidates)
        {
            var full = Path.GetFullPath(c);
            if (File.Exists(full)) return full;
        }

        return "";
    }

    private static string FindScrcpy()
    {
        var baseDir = AppDomain.CurrentDomain.BaseDirectory;
        var candidates = new[]
        {
            Path.Combine(baseDir, "scrcpy", "scrcpy.exe"),
            Path.Combine(baseDir, "scrcpy.exe"),
            "scrcpy.exe",
        };

        foreach (var c in candidates)
        {
            var full = Path.GetFullPath(c);
            if (File.Exists(full)) return full;
        }

        return "";
    }
}

public class AdbDevice
{
    public string Serial { get; set; } = "";
    public string Status { get; set; } = "";
    public string Model { get; set; } = "";
    public string Product { get; set; } = "";
    public override string ToString() =>
        string.IsNullOrEmpty(Model) ? Serial : $"{Serial} [{Model}]";
}
