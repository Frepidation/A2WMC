using A2WMC.Models;

namespace A2WMC.Services;

/// <summary>
/// Cover 图片处理：Base64 → 临时文件
/// </summary>
public static class CoverHelper
{
    private static readonly string CacheDir = Path.Combine(Path.GetTempPath(), "a2wmc_cover");
    private static readonly object Lock = new();

    public static string? SaveCoverToTemp(string base64)
    {
        try
        {
            Directory.CreateDirectory(CacheDir);
            // 清理超过10张的旧图
            Cleanup(10);

            var hash = Convert.ToHexString(
                System.Security.Cryptography.SHA256.HashData(
                    System.Text.Encoding.UTF8.GetBytes(base64)
                )
            )[..16];

            var path = Path.Combine(CacheDir, $"{hash}.jpg");
            if (File.Exists(path)) return path;

            var bytes = Convert.FromBase64String(base64);
            File.WriteAllBytes(path, bytes);
            return path;
        }
        catch
        {
            return null;
        }
    }

    public static void Cleanup(int maxFiles = 10)
    {
        try
        {
            if (!Directory.Exists(CacheDir)) return;
            var files = new DirectoryInfo(CacheDir).GetFiles("*.jpg")
                .OrderByDescending(f => f.LastWriteTime)
                .Skip(maxFiles)
                .ToArray();
            foreach (var f in files)
            {
                try { f.Delete(); } catch { }
            }
        }
        catch { }
    }
}
