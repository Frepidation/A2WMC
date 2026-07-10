using Windows.Media;
using Windows.Media.Core;
using Windows.Media.Playback;
using Windows.Storage.Streams;
using System.Runtime.InteropServices.WindowsRuntime;
using A2WMC.Models;

namespace A2WMC.Services;

/// <summary>
/// SMTC 集成 — 通过 MediaPlayer.SystemMediaTransportControls 自动注册到系统媒体控件。
/// </summary>
public class SmtcService : IDisposable
{
    private MediaPlayer? _mediaPlayer;
    private SystemMediaTransportControls? _smtc;
    private SystemMediaTransportControlsDisplayUpdater? _updater;
    private bool _disposed;

    // 持有一个对封面 RandomAccessStream 的引用，防止 GC 回收
    private InMemoryRandomAccessStream? _coverStream;

    public bool IsInitialized => _smtc != null && _mediaPlayer != null;

    public void Initialize()
    {
        if (_smtc != null) return;
        try
        {
            _mediaPlayer = new MediaPlayer();

            _smtc = _mediaPlayer.SystemMediaTransportControls;
            if (_smtc == null)
            {
                Console.WriteLine("[SMTC] SystemMediaTransportControls is null");
                return;
            }

            _mediaPlayer.CommandManager.IsEnabled = false;

            _smtc.IsEnabled = true;
            _smtc.IsPlayEnabled = true;
            _smtc.IsPauseEnabled = true;
            _smtc.IsNextEnabled = true;
            _smtc.IsPreviousEnabled = true;

            _updater = _smtc.DisplayUpdater;
            _updater.Type = MediaPlaybackType.Music;

            _smtc.ButtonPressed += OnButtonPressed;

            // 监听 MediaPlayer 错误
            _mediaPlayer.MediaFailed += (s, e) =>
            {
                Console.WriteLine($"[SMTC] MediaFailed: {e.ErrorMessage}");
            };
            _mediaPlayer.MediaEnded += (s, e) =>
            {
                Console.WriteLine("[SMTC] MediaEnded");
            };

            // 创建持续播放的流（循环），让 SMTC 会话保持活跃
            // 短 WAV 播完进入 MediaEnded 状态后，Win11 某些版本会停止响应 ButtonPressed
            var silentWav = CreateSilentWav(TimeSpan.FromMilliseconds(2000));
            var stream = new InMemoryRandomAccessStream();
            stream.WriteAsync(silentWav.AsBuffer()).AsTask().Wait();
            stream.Seek(0);
            _mediaPlayer.Source = MediaSource.CreateFromStream(stream, "audio/wav");
            _mediaPlayer.PlaybackSession.Position = TimeSpan.Zero;
            _mediaPlayer.Play();

            // 不要马上 Pause！让流播完，Callback 里自动触发 End 后重新设置状态
            // 先让 MediaPlayer session 稳定注册到系统

            // 同步设置初始状态（等一小会儿让 SMTC 注册完毕）
            Task.Delay(500).ContinueWith(_ =>
            {
                try
                {
                    _mediaPlayer?.Pause();
                    _smtc.PlaybackStatus = MediaPlaybackStatus.Paused;
                    _updater.MusicProperties.Title = "A2WMC";
                    _updater.Thumbnail = null;
                    _updater.Update();
                }
                catch { }
            });

            Console.WriteLine("[SMTC] Initialized");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[SMTC] Init error: {ex.Message}");
            Cleanup();
        }
    }

    public event Action<string>? OnCommand;

    private void OnButtonPressed(SystemMediaTransportControls sender, SystemMediaTransportControlsButtonPressedEventArgs args)
    {
        var btn = args.Button;
        try
        {
            File.AppendAllText(
                Path.Combine(Path.GetTempPath(), "a2wmc_smtc.log"),
                $"{DateTime.Now:HH:mm:ss.fff} [SMTC] ButtonPressed: {btn}{Environment.NewLine}");
        }
        catch { }

        var action = btn switch
        {
            SystemMediaTransportControlsButton.Play => "play",
            SystemMediaTransportControlsButton.Pause => "pause",
            SystemMediaTransportControlsButton.Next => "next",
            SystemMediaTransportControlsButton.Previous => "prev",
            _ => null
        };

        if (action != null)
        {
            try
            {
                File.AppendAllText(
                    Path.Combine(Path.GetTempPath(), "a2wmc_smtc.log"),
                    $"{DateTime.Now:HH:mm:ss.fff} [SMTC] Firing OnCommand: {action}{Environment.NewLine}");
            }
            catch { }

            // 保持 SMTC 按钮状态与命令同步
            if (btn == SystemMediaTransportControlsButton.Play || btn == SystemMediaTransportControlsButton.Pause)
                SetPlaybackStatus(btn == SystemMediaTransportControlsButton.Play);

            OnCommand?.Invoke(action);
        }
    }

    /// <summary>
    /// 更新曲目信息到 SMTC。
    /// </summary>
    public void UpdateTrackInfo(TrackInfo track)
    {
        if (_disposed || _updater == null || _smtc == null) return;

        try
        {
            _updater.MusicProperties.Title = track.Title ?? "";
            _updater.MusicProperties.Artist = track.Artist ?? "";
            _updater.MusicProperties.AlbumTitle = track.Album ?? "";

            // 封面 — 先创建流引用，再设置 Thumbnail
            if (!string.IsNullOrEmpty(track.Cover))
            {
                try
                {
                    var bytes = Convert.FromBase64String(track.Cover);

                    // 创建新的封面流
                    var newStream = new InMemoryRandomAccessStream();
                    using (var ms = new MemoryStream(bytes))
                    {
                        using var img = System.Drawing.Image.FromStream(ms);
                        using var pngMs = new MemoryStream();
                        img.Save(pngMs, System.Drawing.Imaging.ImageFormat.Png);
                        pngMs.Position = 0;
                        // 直接写 byte[] 避免引用问题
                        var pngBytes = pngMs.ToArray();
                        newStream.WriteAsync(pngBytes.AsBuffer()).AsTask().Wait();
                    }
                    newStream.Seek(0);

                    // 设置新引用
                    _updater.Thumbnail = RandomAccessStreamReference.CreateFromStream(newStream);

                    // 再释放旧的流
                    _coverStream?.Dispose();
                    _coverStream = newStream;
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[SMTC] Cover error: {ex.Message}");
                    _coverStream?.Dispose();
                    _coverStream = null;
                    _updater.Thumbnail = null;
                }
            }
            else
            {
                _coverStream?.Dispose();
                _coverStream = null;
                _updater.Thumbnail = null;
            }

            _updater.Update();

            // 同步播放状态
            _smtc.PlaybackStatus = track.State == "playing"
                ? MediaPlaybackStatus.Playing
                : MediaPlaybackStatus.Paused;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[SMTC] Update error: {ex.Message}");
        }
    }

    /// <summary>
    /// 只更新播放状态（播放/暂停），不重新设置元数据。
    /// </summary>
    public void SetPlaybackStatus(bool playing)
    {
        if (_disposed || _smtc == null) return;
        try
        {
            _smtc.PlaybackStatus = playing
                ? MediaPlaybackStatus.Playing
                : MediaPlaybackStatus.Paused;
        }
        catch { }
    }

    /// <summary>
    /// 强制刷新 SMTC（在手机连接后调用，确保隐藏的播放卡片状态同步）。
    /// </summary>
    public void ForceUpdate()
    {
        if (_disposed || _updater == null || _smtc == null) return;
        try
        {
            _updater.Update();
            _smtc.PlaybackStatus = MediaPlaybackStatus.Closed;
            _smtc.PlaybackStatus = MediaPlaybackStatus.Paused;
            _smtc.IsEnabled = true;
        }
        catch { }
    }

    private static byte[] CreateSilentWav(TimeSpan duration)
    {
        var sampleRate = 44100;
        var bitsPerSample = 16;
        var channels = 2; // stereo — SMTC 要求
        var bytesPerSample = bitsPerSample / 8;
        var blockAlign = channels * bytesPerSample;
        var byteRate = sampleRate * blockAlign;
        var totalSamples = (int)(sampleRate * duration.TotalSeconds);
        if (totalSamples < 1) totalSamples = 1;
        var dataSize = totalSamples * blockAlign;

        using var ms = new MemoryStream(44 + dataSize);
        using var bw = new BinaryWriter(ms);

        bw.Write(new[] { (byte)'R', (byte)'I', (byte)'F', (byte)'F' });
        bw.Write(36 + dataSize);
        bw.Write(new[] { (byte)'W', (byte)'A', (byte)'V', (byte)'E' });

        bw.Write(new[] { (byte)'f', (byte)'m', (byte)'t', (byte)' ' });
        bw.Write(16); // PCM
        bw.Write((short)1); // PCM format
        bw.Write((short)channels);
        bw.Write(sampleRate);
        bw.Write(byteRate);
        bw.Write((short)blockAlign);
        bw.Write((short)bitsPerSample);

        bw.Write(new[] { (byte)'d', (byte)'a', (byte)'t', (byte)'a' });
        bw.Write(dataSize);
        bw.Write(new byte[dataSize]); // 静音

        return ms.ToArray();
    }

    private void Cleanup()
    {
        _coverStream?.Dispose();
        _coverStream = null;
        _updater = null;
        _smtc = null;
        if (_mediaPlayer != null)
        {
            try
            {
                _mediaPlayer.Pause();
                _mediaPlayer.Source = null;
                _mediaPlayer.Dispose();
            }
            catch { }
            _mediaPlayer = null;
        }
    }

    public void Dispose()
    {
        if (_disposed) return;
        _disposed = true;
        if (_smtc != null)
        {
            _smtc.ButtonPressed -= OnButtonPressed;
            _smtc.IsEnabled = false;
        }
        Cleanup();
        GC.SuppressFinalize(this);
    }
}
