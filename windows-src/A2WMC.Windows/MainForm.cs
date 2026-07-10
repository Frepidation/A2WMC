using System.Diagnostics;
using A2WMC.Models;
using A2WMC.Services;

namespace A2WMC;

public class MainForm : Form
{
    // ==================== Constants ====================
    private const int DefaultPort = 16921;
    private const int ToggleCooldownMs = 500;

    // ==================== Color Scheme (Material 3 Dark) ====================
    private static readonly Color ColorBg = Color.FromArgb(24, 24, 27);         // surface
    private static readonly Color ColorCard = Color.FromArgb(30, 30, 35);        // card bg
    private static readonly Color ColorCardBorder = Color.FromArgb(52, 52, 58);  // outline
    private static readonly Color ColorPrimary = Color.FromArgb(130, 180, 255);  // primary
    private static readonly Color ColorPrimaryDark = Color.FromArgb(90, 140, 220);
    private static readonly Color ColorOnSurface = Color.FromArgb(230, 230, 235);
    private static readonly Color ColorOnSurfaceDim = Color.FromArgb(150, 150, 160);
    private static readonly Color ColorGreen = Color.FromArgb(76, 200, 120);
    private static readonly Color ColorRed = Color.FromArgb(220, 60, 70);
    private static readonly Color ColorOrange = Color.FromArgb(240, 165, 40);
    private static readonly Color ColorLogBg = Color.FromArgb(16, 16, 18);
    private static readonly Color ColorLogFg = Color.FromArgb(100, 220, 120);
    private static readonly Color ColorInputBg = Color.FromArgb(38, 38, 44);
    private static readonly Color ColorDimText = Color.FromArgb(110, 110, 120);
    private static readonly Color ColorRedFg = Color.FromArgb(240, 80, 80);
    private static readonly Color ColorGrayLine = Color.FromArgb(50, 50, 56);

    // ==================== State ====================
    private bool _bridgeRunning;
    private bool _androidConnected;
    private string _networkHost = "127.0.0.1";
    private int _networkPort = DefaultPort;
    private string _selectedDevice = "";
    private bool _useNetworkMode = true;
    private bool _isPlaying;
    private long _lastToggleTicks;

    // ==================== Services ====================
    private readonly TcpListenerService _tcpService = new();
    private readonly VolumeSynchronizer _volSync = new();
    private AdbHelper? _adb;
    private SmtcService? _smtc;

    // ==================== UI Controls ====================
    // Status card
    private Panel _statusCard = null!;
    private Panel _statusDot = null!;
    private Label _statusText = null!;
    private Label _statusAction = null!;

    // Connection card
    private ComboBox _modeCombo = null!;
    private TextBox _hostInput = null!;
    private TextBox _portInput = null!;
    private ComboBox _deviceCombo = null!;
    private Button _refreshBtn = null!;
    private Label _adbInfoLabel = null!;

    // Permissions card
    private Label _notifStatus = null!;

    // Now Playing card
    private Panel _playingCard = null!;
    private Label _trackTitle = null!;
    private Label _trackArtist = null!;
    private Panel _progressBar = null!;  // thin accent bar

    // Playback controls
    private Button _prevBtn = null!, _playBtn = null!, _nextBtn = null!;

    // Log
    private ListBox _logBox = null!;

    // Scroll container
    private Panel _scrollPanel = null!;

    // Tray
    private NotifyIcon _trayIcon = null!;

    public MainForm()
    {
        Text = "A2WMC";
        Size = new Size(460, 700);
        MinimumSize = new Size(400, 600);
        MaximumSize = new Size(560, 900);
        BackColor = ColorBg;
        ForeColor = ColorOnSurface;
        StartPosition = FormStartPosition.CenterScreen;
        Font = new Font("Segoe UI", 9);
        FormBorderStyle = FormBorderStyle.Sizable;
        Padding = new Padding(16);
        Icon = Icon.ExtractAssociatedIcon(Application.ExecutablePath);

        BuildUI();
        InitServices();
        SetupTray();

        Shown += (_, _) =>
        {
            if (!_useNetworkMode) _ = RefreshDevices();
        };
    }

    // ==================== Init ====================
    private void InitServices()
    {
        _adb = new AdbHelper();

        _tcpService.OnTrackUpdated += OnTrackUpdated;
        _tcpService.OnConnectionChanged += connected =>
        {
            _androidConnected = connected;
            this.Invoke(() => UpdateStatusDisplay());
        };

        _smtc = new SmtcService();
        _smtc.OnCommand += cmd =>
        {
            this.Invoke(() => SendCommand(cmd));
        };
    }

    private void SetupTray()
    {
        _trayIcon = new NotifyIcon
        {
            Icon = Icon,
            Visible = true,
            Text = "A2WMC"
        };
        _trayIcon.DoubleClick += (_, _) =>
        {
            Show();
            WindowState = FormWindowState.Normal;
        };
    }

    // ==================== Helpers: Card Panel ====================
    private static Panel MakeCard(int y, int width)
    {
        return new Panel
        {
            Location = new Point(0, y),
            Size = new Size(width, 100),
            BackColor = ColorCard,
            Padding = new Padding(16)
        };
    }

    // Custom paint for rounded card corners (simple approach: just use padding+border color)
    private static void PaintCardBorder(Panel p)
    {
        p.Paint += (_, e) =>
        {
            using var pen = new Pen(ColorCardBorder, 1);
            var r = new Rectangle(0, 0, p.Width - 1, p.Height - 1);
            // Draw rounded-ish corners manually — just a rect with slightly rounded
            int radius = 8;
            var g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

            using var path = new System.Drawing.Drawing2D.GraphicsPath();
            path.AddArc(r.X, r.Y, radius, radius, 180, 90);
            path.AddArc(r.Right - radius, r.Y, radius, radius, 270, 90);
            path.AddArc(r.Right - radius, r.Bottom - radius, radius, radius, 0, 90);
            path.AddArc(r.X, r.Bottom - radius, radius, radius, 90, 90);
            path.CloseFigure();

            g.DrawPath(pen, path);
        };
    }

    // ==================== UI Build ====================
    private void BuildUI()
    {
        var w = ClientSize.Width - 32; // usable content width

        // ─── scrollable container ───
        _scrollPanel = new Panel
        {
            Location = new Point(0, 0),
            Size = new Size(ClientSize.Width, ClientSize.Height - 8),
            AutoScroll = true,
            BackColor = ColorBg
        };
        // 窗口大小变化时同步 scroll 容器
        Resize += (_, _) =>
        {
            if (_scrollPanel.IsHandleCreated)
                _scrollPanel.Size = new Size(ClientSize.Width, ClientSize.Height - 8);
        };
        _scrollPanel.Resize += (_, _) => Reflow(_scrollPanel);

        var scroll = _scrollPanel;

        // ─── STATUS CARD ───
        _statusCard = MakeCard(0, w); _statusCard.Height = 56;
        PaintCardBorder(_statusCard);
        _statusCard.Resize += (_, _) => _statusCard.Invalidate();

        _statusDot = new Panel
        {
            Location = new Point(16, 21),
            Size = new Size(14, 14),
            BackColor = ColorRed
        };
        _statusDot.Paint += (_, e) =>
        {
            e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            using var brush = new SolidBrush(_statusDot.BackColor);
            e.Graphics.FillEllipse(brush, 0, 0, 14, 14);
        };

        _statusText = new Label
        {
            Location = new Point(38, 18),
            Size = new Size(w - 140, 22),
            Text = "Stopped",
            ForeColor = ColorOnSurfaceDim,
            Font = new Font("Segoe UI", 13, FontStyle.Bold),
            BackColor = Color.Transparent
        };

        _statusAction = new Label
        {
            Location = new Point(w - 90, 18),
            Size = new Size(70, 22),
            Text = "START",
            ForeColor = ColorPrimary,
            Font = new Font("Segoe UI", 11, FontStyle.Bold),
            TextAlign = ContentAlignment.MiddleRight,
            BackColor = Color.Transparent,
            Cursor = Cursors.Hand
        };
        // 确保 Label 不吞掉父容器点击事件 — 整张卡片都能触发 toggle
        _statusCard.MouseClick += (_, _) => ToggleBridge();
        _statusCard.Cursor = Cursors.Hand;
        // Label 自己的点击也是 toggle
        _statusAction.Click += (_, _) => ToggleBridge();

        _statusCard.Controls.AddRange([_statusDot, _statusText, _statusAction]);

        // ─── CONNECTION CARD ───
        var connCard = MakeCard(66, w); connCard.Height = 168;
        PaintCardBorder(connCard);

        var connTitle = MakeSectionLabel("Connection", 16, 0);
        connCard.Controls.Add(connTitle);

        // Mode dropdown (styled)
        _modeCombo = new ComboBox
        {
            Location = new Point(16, 28),
            Size = new Size(w - 32, 30),
            DropDownStyle = ComboBoxStyle.DropDownList,
            FlatStyle = FlatStyle.Flat,
            Items = { "Network Mode", "ADB Mode" },
            SelectedIndex = 1,
            BackColor = ColorInputBg,
            ForeColor = ColorOnSurface
        };
        _modeCombo.SelectedIndexChanged += async (_, _) =>
        {
            _useNetworkMode = _modeCombo.SelectedIndex == 0;
            ShowConnectionFields();
            if (!_useNetworkMode) await RefreshDevices();
        };
        connCard.Controls.Add(_modeCombo);

        // Host input
        _hostInput = new TextBox
        {
            Location = new Point(16, 64),
            Size = new Size(w - 32 - 80, 30),
            Text = "127.0.0.1",
            BackColor = ColorInputBg,
            ForeColor = ColorOnSurface,
            BorderStyle = BorderStyle.FixedSingle,
            Enabled = false
        };
        connCard.Controls.Add(_hostInput);

        // Port input
        _portInput = new TextBox
        {
            Location = new Point(w - 80, 64),
            Size = new Size(64, 30),
            Text = DefaultPort.ToString(),
            BackColor = ColorInputBg,
            ForeColor = ColorOnSurface,
            BorderStyle = BorderStyle.FixedSingle,
            Enabled = false
        };
        connCard.Controls.Add(_portInput);

        // Device dropdown
        _deviceCombo = new ComboBox
        {
            Location = new Point(16, 100),
            Size = new Size(w - 32 - 48, 30),
            DropDownStyle = ComboBoxStyle.DropDownList,
            FlatStyle = FlatStyle.Flat,
            Enabled = true,
            BackColor = ColorInputBg,
            ForeColor = ColorOnSurface
        };
        _deviceCombo.Items.Add("(waiting...)");
        _deviceCombo.SelectedIndex = 0;
        connCard.Controls.Add(_deviceCombo);

        // Refresh button
        _refreshBtn = new Button
        {
            Location = new Point(w - 44, 100),
            Size = new Size(28, 28),
            Text = "\u21BB",
            FlatStyle = FlatStyle.Flat,
            BackColor = Color.FromArgb(45, 45, 50),
            ForeColor = ColorOnSurface,
            Enabled = true
        };
        _refreshBtn.FlatAppearance.BorderSize = 0;
        _refreshBtn.Click += async (_, _) => await RefreshDevices();
        connCard.Controls.Add(_refreshBtn);

        // ADB info
        _adbInfoLabel = new Label
        {
            Location = new Point(16, 134),
            Size = new Size(w - 32, 16),
            ForeColor = ColorDimText,
            Font = new Font("Segoe UI", 8),
            Text = ""
        };
        connCard.Controls.Add(_adbInfoLabel);

        // ─── PERMISSIONS CARD (simplified) ───
        var permCard = MakeCard(244, w); permCard.Height = 64;
        PaintCardBorder(permCard);

        var permTitle = MakeSectionLabel("Permissions", 16, 0);
        permTitle.Size = new Size(w - 32, 20);
        permCard.Controls.Add(permTitle);

        var notifLabel = new Label
        {
            Location = new Point(16, 26),
            Size = new Size(w - 120, 20),
            Text = "Notification listener",
            ForeColor = ColorOnSurface,
            Font = new Font("Segoe UI", 10),
            BackColor = Color.Transparent
        };
        _notifStatus = new Label
        {
            Location = new Point(w - 96, 26),
            Size = new Size(80, 20),
            Text = "Not granted",
            ForeColor = ColorRedFg,
            Font = new Font("Segoe UI", 9),
            TextAlign = ContentAlignment.MiddleRight,
            BackColor = Color.Transparent
        };
        permCard.Controls.AddRange([notifLabel, _notifStatus]);

        // ─── NOW PLAYING CARD ───
        _playingCard = MakeCard(318, w); _playingCard.Height = 72;
        _playingCard.BackColor = Color.FromArgb(26, 26, 32);
        PaintCardBorder(_playingCard);
        _playingCard.Paint += (_, e) =>
        {
            // accent bar along bottom
            using var brush = new SolidBrush(Color.FromArgb(40, ColorPrimary));
            e.Graphics.FillRectangle(brush, 8, _playingCard.Height - 3, _playingCard.Width - 16, 3);
        };
        _playingCard.Visible = false;

        _trackTitle = new Label
        {
            Location = new Point(16, 16),
            Size = new Size(w - 32, 20),
            Text = "No track",
            ForeColor = ColorOnSurface,
            Font = new Font("Segoe UI", 11, FontStyle.Bold),
            BackColor = Color.Transparent
        };
        _trackArtist = new Label
        {
            Location = new Point(16, 38),
            Size = new Size(w - 32, 16),
            Text = "",
            ForeColor = ColorOnSurfaceDim,
            Font = new Font("Segoe UI", 9),
            BackColor = Color.Transparent
        };
        _progressBar = new Panel
        {
            Location = new Point(8, 72 - 3),
            Size = new Size(0, 3),
            BackColor = ColorPrimary
        };
        _playingCard.Controls.AddRange([_trackTitle, _trackArtist, _progressBar]);

        // ─── PLAYBACK CONTROLS ───
        var ctrlY = 400;
        var ctrlPanel = new Panel
        {
            Location = new Point(0, ctrlY),
            Size = new Size(w, 52),
            BackColor = Color.Transparent
        };

        int btnSize = 40;
        int spacing = 16;
        int totalW = btnSize * 3 + spacing * 2;
        int startX = (w - totalW) / 2;

        _prevBtn = MakeCtrlButton("\u23EE", startX, 6, btnSize);
        _prevBtn.Enabled = false;
        _playBtn = MakeCtrlButton("\u25B6", startX + btnSize + spacing, 6, btnSize);
        _playBtn.Enabled = false;
        _nextBtn = MakeCtrlButton("\u23ED", startX + (btnSize + spacing) * 2, 6, btnSize);
        _nextBtn.Enabled = false;

        _prevBtn.Click += (_, _) => SendCommand("prev");
        _playBtn.Click += (_, _) => SendCommand("play");
        _nextBtn.Click += (_, _) => SendCommand("next");

        ctrlPanel.Controls.AddRange([_prevBtn, _playBtn, _nextBtn]);

        // ─── LOG ───
        var logY = ctrlY + 56;
        var logTitle = new Label
        {
            Location = new Point(0, logY),
            Size = new Size(w, 18),
            Text = "  Log",
            ForeColor = ColorDimText,
            Font = new Font("Segoe UI", 8, FontStyle.Bold),
            BackColor = Color.Transparent
        };

        _logBox = new ListBox
        {
            Location = new Point(0, logY + 20),
            Size = new Size(w, 180),
            BackColor = ColorLogBg,
            ForeColor = ColorLogFg,
            BorderStyle = BorderStyle.None,
            Font = new Font("Consolas", 8),
            IntegralHeight = false,
            HorizontalScrollbar = true
        };

        // ─── add to scroll container ───
        scroll.Controls.AddRange([
            _statusCard, connCard, permCard,
            _playingCard, ctrlPanel, logTitle, _logBox
        ]);

        Controls.Add(scroll);

        // initial pass
        ShowConnectionFields();
        UpdateAdbInfo();
    }

    private void Reflow(Panel scroll)
    {
        var w = scroll.ClientSize.Width - 32; // scroll bar compensation
        if (w < 300) w = 300;

        _statusCard.Width = w;
        _statusAction.Location = new Point(w - 90, 18);
        _statusCard.Invalidate();

        // connection card (index 1 in controls)
        var connCard = (Panel)scroll.Controls[1];
        connCard.Width = w;
        connCard.Invalidate();
        _modeCombo.Width = w - 32;
        _hostInput.Width = w - 32 - 80;
        _portInput.Location = new Point(w - 80, 64);
        _deviceCombo.Width = w - 32 - 48;
        _refreshBtn.Location = new Point(w - 44, 100);
        _adbInfoLabel.Width = w - 32;

        // perm card
        var permCard = (Panel)scroll.Controls[2];
        permCard.Width = w;
        permCard.Invalidate();
        _notifStatus.Location = new Point(w - 96, 26);

        // now playing
        _playingCard.Width = w;
        _trackTitle.Width = w - 32;
        _trackArtist.Width = w - 32;

        // controls center
        int btnSize = 40;
        int spacing = 16;
        int totalW = btnSize * 3 + spacing * 2;
        int startX = (w - totalW) / 2;
        _prevBtn.Location = new Point(startX, 6);
        _playBtn.Location = new Point(startX + btnSize + spacing, 6);
        _nextBtn.Location = new Point(startX + (btnSize + spacing) * 2, 6);

        // log
        _logBox.Width = w;
    }

    // ==================== Helpers: UI parts ====================
    private static Label MakeSectionLabel(string text, int x, int y)
    {
        return new Label
        {
            Location = new Point(x, y),
            Size = new Size(200, 22),
            Text = text,
            ForeColor = ColorPrimary,
            Font = new Font("Segoe UI", 10, FontStyle.Bold),
            BackColor = Color.Transparent
        };
    }

    private static Button MakeCtrlButton(string text, int x, int y, int size)
    {
        return new Button
        {
            Location = new Point(x, y),
            Size = new Size(size, size),
            Text = text,
            FlatStyle = FlatStyle.Flat,
            BackColor = Color.FromArgb(40, 40, 46),
            ForeColor = ColorOnSurface,
            Font = new Font("Segoe UI", 14),
            Enabled = true
        };
    }

    private void ShowConnectionFields()
    {
        _hostInput.Enabled = _useNetworkMode;
        _portInput.Enabled = _useNetworkMode;
        _deviceCombo.Enabled = !_useNetworkMode;
        _refreshBtn.Enabled = !_useNetworkMode;
    }

    // ==================== Status Display ====================
    private void UpdateStatusDisplay()
    {
        if (_bridgeRunning)
        {
            if (_androidConnected)
            {
                _statusText.Text = "Connected";
                _statusText.ForeColor = ColorGreen;
                _statusDot.BackColor = ColorGreen;
            }
            else
            {
                _statusText.Text = "Waiting for device...";
                _statusText.ForeColor = ColorOrange;
                _statusDot.BackColor = ColorOrange;
            }
            _statusAction.Text = "STOP";
            _statusAction.ForeColor = ColorRed;
        }
        else
        {
            _statusText.Text = "Stopped";
            _statusText.ForeColor = ColorOnSurfaceDim;
            _statusDot.BackColor = ColorRed;
            _statusAction.Text = "START";
            _statusAction.ForeColor = ColorPrimary;
        }
        _statusDot.Invalidate();
    }

    private void UpdateAdbInfo()
    {
        if (_adb == null) return;
        var info = "";
        if (!_adb.AdbAvailable) info = "adb not found in PATH";
        else if (!_adb.ScrcpyAvailable) info = "adb OK, scrcpy not found";
        else info = "adb + scrcpy ready";
        _adbInfoLabel.Text = info;
    }

    // ==================== Toggle Bridge ====================
    private void ToggleBridge()
    {
        var now = Stopwatch.GetTimestamp();
        if ((now - _lastToggleTicks) * 1000 / Stopwatch.Frequency < ToggleCooldownMs) return;
        _lastToggleTicks = now;

        if (_bridgeRunning) StopBridge();
        else StartBridge();
    }

    private async void StartBridge()
    {
        if (_bridgeRunning || !IsHandleCreated) return;
        _useNetworkMode = _modeCombo.SelectedIndex == 0;

        if (_useNetworkMode)
        {
            _networkHost = _hostInput.Text.Trim();
            if (string.IsNullOrEmpty(_networkHost)) _networkHost = "127.0.0.1";
            int.TryParse(_portInput.Text.Trim(), out _networkPort);
            if (_networkPort <= 0) _networkPort = DefaultPort;
        }
        else
        {
            _selectedDevice = "";
            var selText = _deviceCombo.SelectedItem?.ToString();
            if (!string.IsNullOrEmpty(selText) && selText != "(no devices)" && selText != "(waiting...)")
                _selectedDevice = selText.Split(' ')[0];
            if (string.IsNullOrEmpty(_selectedDevice) && selText == "(waiting...)")
            {
                Log("ADB: scanning devices...");
                await RefreshDevices();
                selText = _deviceCombo.SelectedItem?.ToString();
                if (!string.IsNullOrEmpty(selText) && selText != "(no devices)" && selText != "(waiting...)")
                    _selectedDevice = selText.Split(' ')[0];
            }
            if (string.IsNullOrEmpty(_selectedDevice))
            {
                Log("Error: no device selected");
                return;
            }
            _networkHost = "127.0.0.1";
            _networkPort = DefaultPort;
        }

        SetEnabled(false);
        Log(_useNetworkMode
            ? $"Starting (Network) {_networkHost}:{_networkPort}..."
            : $"Starting (ADB) device={_selectedDevice}...");

        _statusText.Text = "Starting...";
        _statusText.ForeColor = ColorOrange;
        _statusDot.BackColor = ColorOrange;
        _statusDot.Invalidate();

        try
        {
            if (!_useNetworkMode && _adb != null)
            {
                Log("ADB: starting scrcpy...");
                _adb.KillScrcpy();
                if (!_adb.StartScrcpy(_selectedDevice))
                {
                    Log("Error: scrcpy failed to start");
                    SetEnabled(true);
                    return;
                }
                await Task.Delay(2000);
                if (!_adb.IsScrcpyRunning())
                    Log("Warning: scrcpy exited, but continuing...");
                Log("ADB: setting up reverse tunnel...");
                var ok = await _adb.ReverseTunnelAsync(_selectedDevice, _networkPort, _networkPort);
                Log(ok ? "ADB: reverse tunnel OK" : "ADB: reverse tunnel failed");
            }

            _bridgeRunning = true;
            _tcpService.ResetConnectionState();
            _volSync.Start();
            _smtc?.Initialize();
            Log(_smtc?.IsInitialized == true ? "SMTC initialized" : "SMTC init failed");

            _ = Task.Run(async () =>
            {
                try { await _tcpService.StartAsync(); }
                catch (Exception ex) when (ex is not OperationCanceledException)
                { Log($"TCP error: {ex.Message}"); }
            });

            if (_useNetworkMode)
                Log($"Listening on {_networkHost}:{_networkPort}");
            else
                Log("Listening on 127.0.0.1:16921 (ADB tunnel)");
        }
        catch (Exception ex)
        {
            Log($"Error: {ex.Message}");
            _bridgeRunning = false;
        }
        finally
        {
            UpdateStatusDisplay();
            SetEnabled(true);
        }
    }

    private void StopBridge()
    {
        _bridgeRunning = false;
        Log("Stopping...");
        _tcpService.Stop();
        _smtc?.Dispose();
        _volSync.Dispose();

        if (!_useNetworkMode && _adb != null)
        {
            _ = _adb.RemoveReverseTunnelAsync(_selectedDevice, _networkPort);
            _adb.KillScrcpy();
        }

        UpdateStatusDisplay();
        SetEnabled(true);
        Log("Bridge stopped.");
    }

    // ==================== UI State ====================
    private void SetEnabled(bool enabled)
    {
        if (!IsHandleCreated) return;
        this.Invoke(() =>
        {
            _modeCombo.Enabled = enabled;
            ShowConnectionFields();
            // bridge 运行时播放按钮启用，停止时禁用
            _prevBtn.Enabled = _bridgeRunning;
            _playBtn.Enabled = _bridgeRunning;
            _nextBtn.Enabled = _bridgeRunning;
        });
    }

    // ==================== Send Command ====================
    private void SendCommand(string action)
    {
        if (!_bridgeRunning) return;
        _ = _tcpService.SendCommandAsync(action);
        Log($"Sent: {action}");
    }

    // ==================== ADB Device List ====================
    private async Task RefreshDevices()
    {
        if (_adb == null) return;
        try
        {
            var devices = await _adb.ListDevicesAsync();
            _deviceCombo.Items.Clear();
            foreach (var d in devices)
                _deviceCombo.Items.Add(d.ToString());
            if (devices.Count == 0)
                _deviceCombo.Items.Add("(no devices)");
            _deviceCombo.SelectedIndex = 0;
            Log($"Found {devices.Count} device(s)");
        }
        catch (Exception ex)
        {
            Log($"ADB error: {ex.Message}");
            _deviceCombo.Items.Clear();
            _deviceCombo.Items.Add("(no devices)");
            _deviceCombo.SelectedIndex = 0;
        }
    }

    // ==================== Track Update ====================
    private void OnTrackUpdated(TrackInfo track)
    {
        try
        {
            this.Invoke(() =>
            {
                var title = track.Title ?? "";
                var artist = track.Artist ?? "";
                _isPlaying = track.State == "playing";

                if (!string.IsNullOrEmpty(title))
                {
                    _playingCard.Visible = true;
                    _trackTitle.Text = title;
                    _trackArtist.Text = !string.IsNullOrEmpty(artist) ? artist : "";

                    // Update tray
                    var icon = _isPlaying ? "\u25B6" : "\u23F8";
                    _trayIcon.Text = $"{icon} {title}";
                }
                else
                {
                    _playingCard.Visible = false;
                }

                if (track.Volume >= 0)
                    Log($"Track: {title}" + (!string.IsNullOrEmpty(artist) ? $" | {artist}" : ""));
            });

            _smtc?.UpdateTrackInfo(track);
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[UI] Track update error: {ex.Message}");
        }
    }

    // ==================== Log ====================
    private void Log(string msg)
    {
        try
        {
            if (!IsHandleCreated) return;
            this.Invoke(() =>
            {
                var line = $"[{DateTime.Now:HH:mm:ss}] {msg}";
                _logBox.Items.Add(line);
                _logBox.TopIndex = _logBox.Items.Count - 1;
                while (_logBox.Items.Count > 500)
                    _logBox.Items.RemoveAt(0);
            });
        }
        catch { }
    }

    // ==================== Cleanup ====================
    protected override void OnFormClosing(FormClosingEventArgs e)
    {
        _trayIcon.Visible = false;
        StopBridge();
        _adb?.Dispose();
        base.OnFormClosing(e);
    }
}
