using System.Text.Json.Serialization;

namespace A2WMC.Models;

/// <summary>
/// 从 Android 端接收的元数据，与 Kotlin TrackInfo 一一对应。
/// 所有属性名必须小写以匹配安卓端 JSON 输出。
/// </summary>
public class TrackInfo
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = "metadata";

    [JsonPropertyName("title")]
    public string Title { get; set; } = "";

    [JsonPropertyName("artist")]
    public string Artist { get; set; } = "";

    [JsonPropertyName("album")]
    public string Album { get; set; } = "";

    [JsonPropertyName("duration")]
    public long Duration { get; set; }

    [JsonPropertyName("position")]
    public long Position { get; set; }

    [JsonPropertyName("state")]
    public string State { get; set; } = "stopped";

    [JsonPropertyName("package")]
    public string? Package { get; set; }

    [JsonPropertyName("cover")]
    public string? Cover { get; set; }

    [JsonPropertyName("volume")]
    public int Volume { get; set; } = -1;

    [JsonPropertyName("maxVolume")]
    public int MaxVolume { get; set; } = 15;
}

/// <summary>
/// 从 Android 端接收的纯音量消息。
/// </summary>
public class VolumeInfo
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = "volume";

    [JsonPropertyName("volume")]
    public int Volume { get; set; }

    [JsonPropertyName("maxVolume")]
    public int MaxVolume { get; set; }
}

/// <summary>
/// 从 Windows 发往 Android 的控制命令。
/// </summary>
public class ControlCommand
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = "command";

    [JsonPropertyName("action")]
    public string Action { get; set; } = "";
}
