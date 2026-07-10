namespace A2WMC.Services;

/// <summary>
/// 应用退出辅助（处理 Ctrl+C、系统关机等）
/// </summary>
public static class ShutdownHelper
{
    private static Action? _handler;

    public static void SetHandler(Action handler)
    {
        _handler = handler;
        // Console.CancelKeyPress 在 WinForms 中不适用，
        // 交给 FormClosing 事件处理
    }

    public static void RequestShutdown()
    {
        _handler?.Invoke();
    }
}
