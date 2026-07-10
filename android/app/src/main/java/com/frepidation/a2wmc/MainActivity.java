package com.frepidation.a2wmc;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.frepidation.a2wmc.service.BridgeService;
import com.frepidation.a2wmc.service.NotifListenerService;
import com.google.android.material.card.MaterialCardView;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0006H\u0002J\u0012\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\b\u0010\u001a\u001a\u00020\u0017H\u0014J\b\u0010\u001b\u001a\u00020\u0017H\u0002J\b\u0010\u001c\u001a\u00020\u0017H\u0002J\b\u0010\u001d\u001a\u00020\u0017H\u0002J\b\u0010\u001e\u001a\u00020\u0017H\u0002J\b\u0010\u001f\u001a\u00020\u0017H\u0002J\u0010\u0010 \u001a\u00020\u00172\u0006\u0010!\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0010\u0012\f\u0012\n \r*\u0004\u0018\u00010\f0\f0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/frepidation/a2wmc/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "hostInput", "Landroid/widget/TextView;", "isRunning", "", "isToggling", "mainHandler", "Landroid/os/Handler;", "notifPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "kotlin.jvm.PlatformType", "notifStatus", "portInput", "statusActionLabel", "statusCard", "Lcom/google/android/material/card/MaterialCardView;", "statusDot", "statusText", "hasNotifAccess", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "openNotifSettings", "refreshNotifAccessStatus", "startBridge", "stopBridge", "toggleBridge", "updateUI", "running", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class MainActivity extends AppCompatActivity {
    private TextView hostInput;
    private boolean isRunning;
    private boolean isToggling;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ActivityResultLauncher<Intent> notifPermissionLauncher;
    private TextView notifStatus;
    private TextView portInput;
    private TextView statusActionLabel;
    private MaterialCardView statusCard;
    private TextView statusDot;
    private TextView statusText;

    public MainActivity() {
        ActivityResultLauncher<Intent> activityResultLauncherRegisterForActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.frepidation.a2wmc.MainActivity$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                MainActivity.notifPermissionLauncher$lambda$0(this.f$0, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(activityResultLauncherRegisterForActivityResult, "registerForActivityResult(...)");
        this.notifPermissionLauncher = activityResultLauncherRegisterForActivityResult;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void notifPermissionLauncher$lambda$0(MainActivity this$0, ActivityResult it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.refreshNotifAccessStatus();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(1280);
        }
        setContentView(R.layout.activity_main);
        View viewFindViewById = findViewById(R.id.status_card);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById, "findViewById(...)");
        this.statusCard = (MaterialCardView) viewFindViewById;
        View viewFindViewById2 = findViewById(R.id.status_text);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById2, "findViewById(...)");
        this.statusText = (TextView) viewFindViewById2;
        View viewFindViewById3 = findViewById(R.id.status_dot);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById3, "findViewById(...)");
        this.statusDot = (TextView) viewFindViewById3;
        View viewFindViewById4 = findViewById(R.id.status_action_label);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById4, "findViewById(...)");
        this.statusActionLabel = (TextView) viewFindViewById4;
        View viewFindViewById5 = findViewById(R.id.notif_access_status);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById5, "findViewById(...)");
        this.notifStatus = (TextView) viewFindViewById5;
        View viewFindViewById6 = findViewById(R.id.host_input);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById6, "findViewById(...)");
        this.hostInput = (TextView) viewFindViewById6;
        View viewFindViewById7 = findViewById(R.id.port_input);
        Intrinsics.checkNotNullExpressionValue(viewFindViewById7, "findViewById(...)");
        this.portInput = (TextView) viewFindViewById7;
        refreshNotifAccessStatus();
        MaterialCardView materialCardView = this.statusCard;
        if (materialCardView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusCard");
            materialCardView = null;
        }
        materialCardView.setOnClickListener(new View.OnClickListener() { // from class: com.frepidation.a2wmc.MainActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$1(this.f$0, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$1(MainActivity this$0, View it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.toggleBridge();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        refreshNotifAccessStatus();
    }

    private final boolean hasNotifAccess() {
        String nls = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        ComponentName cn = new ComponentName(this, (Class<?>) NotifListenerService.class);
        if (nls == null) {
            return false;
        }
        String strFlattenToString = cn.flattenToString();
        Intrinsics.checkNotNullExpressionValue(strFlattenToString, "flattenToString(...)");
        return StringsKt.contains$default((CharSequence) nls, (CharSequence) strFlattenToString, false, 2, (Object) null);
    }

    private final void openNotifSettings() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        this.notifPermissionLauncher.launch(intent);
    }

    private final void refreshNotifAccessStatus() {
        boolean hasAccess = hasNotifAccess();
        TextView textView = this.notifStatus;
        TextView textView2 = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("notifStatus");
            textView = null;
        }
        textView.setText(hasAccess ? "Granted" : "Not granted");
        TextView textView3 = this.notifStatus;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("notifStatus");
        } else {
            textView2 = textView3;
        }
        textView2.setTextColor(getResources().getColor(hasAccess ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));
    }

    private final void toggleBridge() {
        if (this.isToggling) {
            return;
        }
        this.isToggling = true;
        this.mainHandler.postDelayed(new Runnable() { // from class: com.frepidation.a2wmc.MainActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.toggleBridge$lambda$2(this.f$0);
            }
        }, 800L);
        if (this.isRunning) {
            stopBridge();
        } else if (!hasNotifAccess()) {
            this.isToggling = false;
            openNotifSettings();
        } else {
            startBridge();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void toggleBridge$lambda$2(MainActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.isToggling = false;
    }

    private final void startBridge() {
        try {
            Intent intent = new Intent(this, (Class<?>) BridgeService.class);
            TextView textView = this.hostInput;
            TextView textView2 = null;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hostInput");
                textView = null;
            }
            String host = StringsKt.trim((CharSequence) textView.getText().toString()).toString();
            TextView textView3 = this.portInput;
            if (textView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("portInput");
            } else {
                textView2 = textView3;
            }
            String portStr = StringsKt.trim((CharSequence) textView2.getText().toString()).toString();
            Integer intOrNull = StringsKt.toIntOrNull(portStr);
            int port = intOrNull != null ? intOrNull.intValue() : 16921;
            if ((host.length() == 0) || Intrinsics.areEqual(host, "127.0.0.1")) {
                intent.putExtra("mode", "adb");
                intent.putExtra("host", "127.0.0.1");
                intent.putExtra("port", port);
            } else {
                intent.putExtra("mode", "wifi");
                intent.putExtra("host", host);
                intent.putExtra("port", port);
            }
            startForegroundService(intent);
            this.isRunning = true;
            updateUI(true);
        } catch (Exception e) {
            this.isRunning = false;
            updateUI(false);
        }
    }

    private final void stopBridge() {
        try {
            Intent intent = new Intent(this, (Class<?>) BridgeService.class);
            stopService(intent);
        } catch (Exception e) {
        }
        this.isRunning = false;
        updateUI(false);
    }

    private final void updateUI(boolean running) {
        TextView textView = this.statusText;
        TextView textView2 = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusText");
            textView = null;
        }
        textView.setText(running ? "Running" : "Stopped");
        TextView textView3 = this.statusDot;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusDot");
            textView3 = null;
        }
        textView3.setBackgroundResource(running ? R.drawable.circle_green : R.drawable.circle_red);
        TextView textView4 = this.statusActionLabel;
        if (textView4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusActionLabel");
            textView4 = null;
        }
        textView4.setText(running ? "STOP" : "START");
        TextView textView5 = this.hostInput;
        if (textView5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hostInput");
            textView5 = null;
        }
        textView5.setEnabled(!running);
        TextView textView6 = this.portInput;
        if (textView6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("portInput");
        } else {
            textView2 = textView6;
        }
        textView2.setEnabled(!running);
    }
}
