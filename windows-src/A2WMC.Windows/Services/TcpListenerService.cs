using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using A2WMC.Models;

namespace A2WMC.Services;

/// <summary>
/// TCP 服务端 — 监听 Android 端连接，接收元数据，发送控制命令。
/// </summary>
public class TcpListenerService : IDisposable
{
    private const int Port = 16921;
    private TcpListener? _listener;
    private TcpClient? _client;
    private StreamReader? _reader;
    private StreamWriter? _writer;
    private CancellationTokenSource? _cts;
    private readonly Queue<string> _pendingCommands = new();
    private readonly object _lock = new();
    private readonly JsonSerializerOptions _jsonOptions = new()
    {
        PropertyNameCaseInsensitive = true
    };

    public event Action<TrackInfo>? OnTrackUpdated;
    public event Action<int, int>? OnVolumeUpdated;
    public event Action<int>? OnMaxVolumeUpdated;
    public event Action<bool>? OnConnectionChanged;

    private DateTime _lastConnectedAt = DateTime.MinValue;
    private bool _isConnected;
    private static readonly TimeSpan DebounceInterval = TimeSpan.FromSeconds(2);

    public void ResetConnectionState()
    {
        _isConnected = false;
        _lastConnectedAt = DateTime.MinValue;
    }

    public async Task StartAsync()
    {
        _cts = new CancellationTokenSource();
        _listener = new TcpListener(IPAddress.Any, Port);
        _listener.Start();
        Console.WriteLine($"[TCP] Listening on port {Port}...");

        while (!_cts.IsCancellationRequested)
        {
            try
            {
                _client = await _listener.AcceptTcpClientAsync(_cts.Token);
                Console.WriteLine($"[TCP] New connection: {_client.Client.RemoteEndPoint}");

                var stream = _client.GetStream();
                _reader = new StreamReader(stream, Encoding.UTF8);
                _writer = new StreamWriter(stream, Encoding.UTF8) { NewLine = "\n", AutoFlush = true };

                // 发送队列中积压的命令
                lock (_lock)
                {
                    while (_pendingCommands.TryDequeue(out var pending))
                    {
                        var cmd = new ControlCommand { Type = "command", Action = pending };
                        var json = JsonSerializer.Serialize(cmd);
                        _writer.WriteLine(json);
                        Console.WriteLine($"[TCP] Dequeued & sent: {pending}");
                    }
                }

                while (!_cts.IsCancellationRequested)
                {
                    var line = await _reader.ReadLineAsync(_cts.Token);
                    if (line == null) break;

                    if (string.IsNullOrWhiteSpace(line)) continue;

                    try
                    {
                        using var doc = JsonDocument.Parse(line);
                        var root = doc.RootElement;
                        var type = root.TryGetProperty("type", out var typeProp) ? typeProp.GetString() : null;

                        var now = DateTime.UtcNow;
                        if (!_isConnected && (now - _lastConnectedAt) > DebounceInterval)
                        {
                            _isConnected = true;
                            OnConnectionChanged?.Invoke(true);
                        }
                        _lastConnectedAt = now;

                        switch (type)
                        {
                            case "metadata":
                                var track = JsonSerializer.Deserialize<TrackInfo>(line, _jsonOptions);
                                if (track != null)
                                {
                                    Console.WriteLine($"[TCP] metadata: {track.Title} v={track.Volume}/{track.MaxVolume}");
                                    OnTrackUpdated?.Invoke(track);
                                    if (track.Volume >= 0)
                                        OnVolumeUpdated?.Invoke(track.Volume, track.MaxVolume);
                                }
                                break;

                            case "volume":
                                var vol = JsonSerializer.Deserialize<VolumeInfo>(line, _jsonOptions);
                                if (vol != null)
                                {
                                    Console.WriteLine($"[TCP] volume: {vol.Volume}/{vol.MaxVolume}");
                                    OnVolumeUpdated?.Invoke(vol.Volume, vol.MaxVolume);
                                }
                                break;

                            case "maxvol":
                                var maxv = root.TryGetProperty("maxVolume", out var maxProp)
                                    ? maxProp.GetInt32() : 15;
                                Console.WriteLine($"[TCP] maxvol: {maxv}");
                                OnMaxVolumeUpdated?.Invoke(maxv);
                                break;

                            default:
                                Console.WriteLine($"[TCP] unknown type: {type}");
                                break;
                        }
                    }
                    catch (JsonException ex)
                    {
                        Console.WriteLine($"[TCP] JSON err: {ex.Message}");
                    }
                }
            }
            catch (OperationCanceledException) { break; }
            catch (Exception ex)
            {
                Console.WriteLine($"[TCP] Error: {ex.Message}");
            }
            finally
            {
                if (_isConnected)
                {
                    _isConnected = false;
                    OnConnectionChanged?.Invoke(false);
                }
                DisconnectClient();
                Console.WriteLine("[TCP] Disconnected, waiting...");
            }
        }
    }

    public async Task SendCommandAsync(string action)
    {
        try
        {
            if (_writer != null)
            {
                var cmd = new ControlCommand { Type = "command", Action = action };
                await _writer.WriteLineAsync(JsonSerializer.Serialize(cmd));
                Console.WriteLine($"[TCP] Sent: {action}");
                return;
            }

            // writer 还没就绪，入队列等下次连接
            lock (_lock)
            {
                _pendingCommands.Enqueue(action);
                Console.WriteLine($"[TCP] Queued: {action} (no connection yet)");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[TCP] Send err: {ex.Message}");
        }
    }

    public void Stop()
    {
        _cts?.Cancel();
        DisconnectClient();
        _listener?.Stop();
        Console.WriteLine("[TCP] Stopped.");
    }

    private void DisconnectClient()
    {
        _reader?.Dispose();
        _writer?.Dispose();
        _client?.Dispose();
        _reader = null;
        _writer = null;
        _client = null;
    }

    public void Dispose()
    {
        Stop();
        _cts?.Dispose();
        _listener?.Dispose();
        GC.SuppressFinalize(this);
    }
}
