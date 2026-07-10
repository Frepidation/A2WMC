package com.frepidation.a2wmc.service;

import android.R;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import com.frepidation.a2wmc.model.TrackInfo;
import com.frepidation.a2wmc.transport.ControlChannel;
import com.frepidation.a2wmc.transport.MetadataProtocol;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import org.json.JSONException;

/* JADX INFO: compiled from: BridgeService.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u0099\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0012*\u0001\b\u0018\u0000 K2\u00020\u0001:\u0001KB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010!\u001a\u00020\"H\u0002J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u0011H\u0002J\b\u0010&\u001a\u00020\"H\u0002J\u0012\u0010'\u001a\u0004\u0018\u00010(2\u0006\u0010)\u001a\u00020*H\u0002J\u0010\u0010+\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020*H\u0002J\b\u0010,\u001a\u00020-H\u0002J\b\u0010.\u001a\u00020-H\u0002J\u0010\u0010/\u001a\u00020\"2\u0006\u00100\u001a\u000201H\u0002J\u0014\u00102\u001a\u0004\u0018\u0001032\b\u00104\u001a\u0004\u0018\u000105H\u0016J\b\u00106\u001a\u00020\"H\u0016J\b\u00107\u001a\u00020\"H\u0016J\u0016\u00108\u001a\u00020\"2\f\u00109\u001a\b\u0012\u0004\u0012\u00020\u000b0:H\u0002J\"\u0010;\u001a\u00020-2\b\u00104\u001a\u0004\u0018\u0001052\u0006\u0010<\u001a\u00020-2\u0006\u0010=\u001a\u00020-H\u0016J\u0010\u0010>\u001a\u00020\"2\u0006\u0010?\u001a\u00020\u000fH\u0002J\b\u0010@\u001a\u00020\"H\u0002J'\u0010A\u001a\u00020\"2\u0006\u0010)\u001a\u00020*2\u0006\u0010B\u001a\u00020\u00112\b\u0010C\u001a\u0004\u0018\u00010-H\u0002¢\u0006\u0002\u0010DJ\u0010\u0010E\u001a\u00020\"2\u0006\u0010F\u001a\u00020-H\u0002J\b\u0010G\u001a\u00020\"H\u0002J\b\u0010H\u001a\u00020\"H\u0002J\b\u0010I\u001a\u00020\"H\u0002J\b\u0010J\u001a\u00020\"H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\tR\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006L"}, d2 = {"Lcom/frepidation/a2wmc/service/BridgeService;", "Landroid/app/Service;", "()V", "audioManager", "Landroid/media/AudioManager;", "controlChannel", "Lcom/frepidation/a2wmc/transport/ControlChannel;", "controllerCallback", "com/frepidation/a2wmc/service/BridgeService$controllerCallback$1", "Lcom/frepidation/a2wmc/service/BridgeService$controllerCallback$1;", "currentController", "Landroid/media/session/MediaController;", "heartbeatJob", "Lkotlinx/coroutines/Job;", "lastSent", "Lcom/frepidation/a2wmc/model/TrackInfo;", "lastSentJson", "", "listenerComponent", "Landroid/content/ComponentName;", "getListenerComponent", "()Landroid/content/ComponentName;", "listenerComponent$delegate", "Lkotlin/Lazy;", "maxvolHandshakeSent", "", "msm", "Landroid/media/session/MediaSessionManager;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "sessionsChangedListener", "Landroid/media/session/MediaSessionManager$OnActiveSessionsChangedListener;", "volumeSyncJob", "broadcastVolume", "", "buildNotification", "Landroid/app/Notification;", "content", "createNotificationChannel", "extractCover", "Landroid/graphics/Bitmap;", "meta", "Landroid/media/MediaMetadata;", "extractTrackInfo", "getMaxMediaVolume", "", "getMediaVolume", "handleCommand", "cmd", "Lcom/frepidation/a2wmc/transport/MetadataProtocol$ControlCommand;", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onSessionsChanged", "sessions", "", "onStartCommand", "flags", "startId", "sendJson", "info", "sendMaxvolHandshake", "sendTrack", "pkg", "playbackState", "(Landroid/media/MediaMetadata;Ljava/lang/String;Ljava/lang/Integer;)V", "setMediaVolume", "level", "startHeartbeat", "startListening", "startVolumeSync", "stopListening", "Companion", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class BridgeService extends Service {
    private static final String CHANNEL_ID = "a2wmc_bridge";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 16921;
    private static final int NOTIFY_ID = 1001;
    private static final String TAG = "BridgeService";
    private static final long VOLUME_SYNC_INTERVAL = 180000;
    private AudioManager audioManager;
    private ControlChannel controlChannel;
    private MediaController currentController;
    private Job heartbeatJob;
    private String lastSentJson;
    private boolean maxvolHandshakeSent;
    private MediaSessionManager msm;
    private Job volumeSyncJob;
    private final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob$default((Job) null, 1, (Object) null).plus(Dispatchers.getMain()));
    private TrackInfo lastSent = new TrackInfo(null, null, null, 0, 0, null, null, null, 0, 0, 1023, null);

    /* JADX INFO: renamed from: listenerComponent$delegate, reason: from kotlin metadata */
    private final Lazy listenerComponent = LazyKt.lazy(new Function0<ComponentName>() { // from class: com.frepidation.a2wmc.service.BridgeService$listenerComponent$2
        {
            super(0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final ComponentName invoke() {
            return new ComponentName(this.this$0, (Class<?>) NotifListenerService.class);
        }
    });
    private final MediaSessionManager.OnActiveSessionsChangedListener sessionsChangedListener = new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: com.frepidation.a2wmc.service.BridgeService$$ExternalSyntheticLambda0
        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public final void onActiveSessionsChanged(List list) {
            BridgeService.sessionsChangedListener$lambda$0(this.f$0, list);
        }
    };
    private final BridgeService$controllerCallback$1 controllerCallback = new MediaController.Callback() { // from class: com.frepidation.a2wmc.service.BridgeService$controllerCallback$1
        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata metadata) {
            PlaybackState playbackState;
            if (metadata == null) {
                return;
            }
            MediaController mediaController = this.this$0.currentController;
            Integer numValueOf = null;
            String pkg = mediaController != null ? mediaController.getPackageName() : null;
            if (pkg == null) {
                pkg = "";
            }
            BridgeService bridgeService = this.this$0;
            MediaController mediaController2 = bridgeService.currentController;
            if (mediaController2 != null && (playbackState = mediaController2.getPlaybackState()) != null) {
                numValueOf = Integer.valueOf(playbackState.getState());
            }
            bridgeService.sendTrack(metadata, pkg, numValueOf);
            this.this$0.broadcastVolume();
        }

        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            MediaController mediaController;
            MediaMetadata meta;
            if (state != null && (mediaController = this.this$0.currentController) != null && (meta = mediaController.getMetadata()) != null) {
                MediaController mediaController2 = this.this$0.currentController;
                String pkg = mediaController2 != null ? mediaController2.getPackageName() : null;
                if (pkg == null) {
                    pkg = "";
                }
                this.this$0.sendTrack(meta, pkg, Integer.valueOf(state.getState()));
            }
        }

        @Override // android.media.session.MediaController.Callback
        public void onSessionDestroyed() {
            Log.d("BridgeService", "Session destroyed, current controller invalidated");
            MediaController mediaController = this.this$0.currentController;
            if (mediaController != null) {
                mediaController.unregisterCallback(this);
            }
            this.this$0.currentController = null;
        }
    };

    private final ComponentName getListenerComponent() {
        return (ComponentName) this.listenerComponent.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void sessionsChangedListener$lambda$0(BridgeService this$0, List sessions) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.onSessionsChanged(sessions == null ? CollectionsKt.emptyList() : sessions);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");
        createNotificationChannel();
        Notification notification = buildNotification("Starting...");
        startForeground(1001, notification);
        Object systemService = getSystemService("media_session");
        this.msm = systemService instanceof MediaSessionManager ? (MediaSessionManager) systemService : null;
        Object systemService2 = getSystemService("audio");
        AudioManager audioManager = systemService2 instanceof AudioManager ? (AudioManager) systemService2 : null;
        this.audioManager = audioManager;
        Log.i(TAG, "audioManager init: " + audioManager);
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x0015  */
    @Override // android.app.Service
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int onStartCommand(android.content.Intent r16, int r17, int r18) {
        /*
            r15 = this;
            r1 = r15
            r2 = r16
            java.lang.String r0 = "Service onStartCommand"
            java.lang.String r3 = "BridgeService"
            android.util.Log.i(r3, r0)
            if (r2 == 0) goto L15
            java.lang.String r0 = "mode"
            java.lang.String r0 = r2.getStringExtra(r0)     // Catch: java.lang.Exception -> L91
            if (r0 != 0) goto L17
        L15:
            java.lang.String r0 = "adb"
        L17:
            if (r2 == 0) goto L21
            java.lang.String r4 = "host"
            java.lang.String r4 = r2.getStringExtra(r4)     // Catch: java.lang.Exception -> L91
            if (r4 != 0) goto L23
        L21:
            java.lang.String r4 = "127.0.0.1"
        L23:
            r5 = 16921(0x4219, float:2.3711E-41)
            if (r2 == 0) goto L2d
            java.lang.String r6 = "port"
            int r5 = r2.getIntExtra(r6, r5)     // Catch: java.lang.Exception -> L91
        L2d:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L91
            r6.<init>()     // Catch: java.lang.Exception -> L91
            java.lang.String r7 = "桥接 "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Exception -> L91
            java.lang.StringBuilder r6 = r6.append(r0)     // Catch: java.lang.Exception -> L91
            java.lang.String r7 = " 模式 - "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Exception -> L91
            java.lang.StringBuilder r6 = r6.append(r4)     // Catch: java.lang.Exception -> L91
            java.lang.String r7 = ":"
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Exception -> L91
            java.lang.StringBuilder r6 = r6.append(r5)     // Catch: java.lang.Exception -> L91
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Exception -> L91
            android.app.Notification r6 = r15.buildNotification(r6)     // Catch: java.lang.Exception -> L91
            r7 = 1001(0x3e9, float:1.403E-42)
            r15.startForeground(r7, r6)     // Catch: java.lang.Exception -> L91
            com.frepidation.a2wmc.transport.ControlChannel r7 = new com.frepidation.a2wmc.transport.ControlChannel     // Catch: java.lang.Exception -> L91
            com.frepidation.a2wmc.service.BridgeService$onStartCommand$ch$1 r8 = new com.frepidation.a2wmc.service.BridgeService$onStartCommand$ch$1     // Catch: java.lang.Exception -> L91
            r8.<init>()     // Catch: java.lang.Exception -> L91
            kotlin.jvm.functions.Function1 r8 = (kotlin.jvm.functions.Function1) r8     // Catch: java.lang.Exception -> L91
            r7.<init>(r4, r5, r8)     // Catch: java.lang.Exception -> L91
            r1.controlChannel = r7     // Catch: java.lang.Exception -> L91
            kotlinx.coroutines.CoroutineScope r8 = r1.scope     // Catch: java.lang.Exception -> L91
            r7.start(r8)     // Catch: java.lang.Exception -> L91
            r8 = 0
            r1.maxvolHandshakeSent = r8     // Catch: java.lang.Exception -> L91
            kotlinx.coroutines.CoroutineScope r9 = r1.scope     // Catch: java.lang.Exception -> L91
            r10 = 0
            r11 = 0
            com.frepidation.a2wmc.service.BridgeService$onStartCommand$1 r8 = new com.frepidation.a2wmc.service.BridgeService$onStartCommand$1     // Catch: java.lang.Exception -> L91
            r12 = 0
            r8.<init>(r12)     // Catch: java.lang.Exception -> L91
            r12 = r8
            kotlin.jvm.functions.Function2 r12 = (kotlin.jvm.functions.Function2) r12     // Catch: java.lang.Exception -> L91
            r13 = 3
            r14 = 0
            kotlinx.coroutines.BuildersKt.launch$default(r9, r10, r11, r12, r13, r14)     // Catch: java.lang.Exception -> L91
            r15.startListening()     // Catch: java.lang.Exception -> L91
            r15.startHeartbeat()     // Catch: java.lang.Exception -> L91
            r15.startVolumeSync()     // Catch: java.lang.Exception -> L91
            goto L9a
        L91:
            r0 = move-exception
            java.lang.String r4 = "Failed to start service"
            r5 = r0
            java.lang.Throwable r5 = (java.lang.Throwable) r5
            android.util.Log.e(r3, r4, r5)
        L9a:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.service.BridgeService.onStartCommand(android.content.Intent, int, int):int");
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$onStartCommand$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$onStartCommand$1", f = "BridgeService.kt", i = {}, l = {119}, m = "invokeSuspend", n = {}, s = {})
    static final class C00251 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;

        C00251(Continuation<? super C00251> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return BridgeService.this.new C00251(continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C00251) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            C00251 c00251;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    this.label = 1;
                    if (DelayKt.delay(2000L, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    c00251 = this;
                    break;
                    break;
                case 1:
                    c00251 = this;
                    ResultKt.throwOnFailure($result);
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            BridgeService.this.sendMaxvolHandshake();
            return Unit.INSTANCE;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
        stopListening();
        Job job = this.heartbeatJob;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        Job job2 = this.volumeSyncJob;
        if (job2 != null) {
            Job.DefaultImpls.cancel$default(job2, (CancellationException) null, 1, (Object) null);
        }
        ControlChannel controlChannel = this.controlChannel;
        if (controlChannel != null) {
            controlChannel.stop();
        }
        MediaController mediaController = this.currentController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.controllerCallback);
        }
        this.currentController = null;
        CoroutineScopeKt.cancel$default(this.scope, null, 1, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void sendMaxvolHandshake() {
        int maxVol = getMaxMediaVolume();
        String json = "{\"type\":\"maxvol\",\"maxVolume\":" + maxVol + "}";
        Log.i(TAG, "Sending maxvol handshake: " + json);
        BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new C00271(json, null), 3, null);
        this.maxvolHandshakeSent = true;
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$sendMaxvolHandshake$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$sendMaxvolHandshake$1", f = "BridgeService.kt", i = {}, l = {164}, m = "invokeSuspend", n = {}, s = {})
    static final class C00271 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ String $json;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C00271(String str, Continuation<? super C00271> continuation) {
            super(2, continuation);
            this.$json = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return BridgeService.this.new C00271(this.$json, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C00271) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            C00271 c00271;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    ControlChannel controlChannel = BridgeService.this.controlChannel;
                    if (controlChannel != null) {
                        this.label = 1;
                        if (controlChannel.send(this.$json, this) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        c00271 = this;
                    }
                    return Unit.INSTANCE;
                case 1:
                    c00271 = this;
                    ResultKt.throwOnFailure($result);
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    private final int getMediaVolume() {
        Log.d(TAG, "getMediaVolume called, audioManager=" + this.audioManager);
        AudioManager am = this.audioManager;
        if (am == null) {
            Log.w(TAG, "getMediaVolume: audioManager is null!");
            return -1;
        }
        try {
            int vol = am.getStreamVolume(3);
            Log.d(TAG, "getMediaVolume: " + vol);
            return vol;
        } catch (Exception e) {
            Log.e(TAG, "getMediaVolume error", e);
            return -1;
        }
    }

    private final int getMaxMediaVolume() {
        AudioManager am = this.audioManager;
        if (am == null) {
            return 15;
        }
        return am.getStreamMaxVolume(3);
    }

    private final void setMediaVolume(int level) {
        try {
            AudioManager am = this.audioManager;
            if (am == null) {
                Log.w(TAG, "setMediaVolume: audioManager is null");
                return;
            }
            int maxVol = am.getStreamMaxVolume(3);
            int clamped = RangesKt.coerceIn(level, 0, maxVol);
            Log.i(TAG, "setMediaVolume: " + level + " -> " + clamped + " (max " + maxVol + ")");
            am.setStreamVolume(3, clamped, 1);
            Log.i(TAG, "Volume set OK to " + clamped);
            broadcastVolume();
        } catch (SecurityException e) {
            Log.w(TAG, "No permission to set volume", e);
        } catch (Exception e2) {
            Log.e(TAG, "setMediaVolume error", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void broadcastVolume() {
        int vol = getMediaVolume();
        int maxVol = getMaxMediaVolume();
        if (vol < 0) {
            Log.w(TAG, "broadcastVolume: volume=" + vol + " < 0, skipping");
            return;
        }
        TrackInfo trackInfo = this.lastSent;
        this.lastSent = trackInfo.copy((255 & 1) != 0 ? trackInfo.title : null, (255 & 2) != 0 ? trackInfo.artist : null, (255 & 4) != 0 ? trackInfo.album : null, (255 & 8) != 0 ? trackInfo.duration : 0L, (255 & 16) != 0 ? trackInfo.position : 0L, (255 & 32) != 0 ? trackInfo.state : null, (255 & 64) != 0 ? trackInfo.coverBitmap : null, (255 & 128) != 0 ? trackInfo.packageName : null, (255 & 256) != 0 ? trackInfo.volume : vol, (255 & 512) != 0 ? trackInfo.maxVolume : maxVol);
        String json = "{\"type\":\"volume\",\"volume\":" + vol + ",\"maxVolume\":" + maxVol + "}";
        Log.i(TAG, "Broadcasting volume: " + vol + "/" + maxVol);
        BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new AnonymousClass1(json, null), 3, null);
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$broadcastVolume$1, reason: invalid class name */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$broadcastVolume$1", f = "BridgeService.kt", i = {}, l = {237}, m = "invokeSuspend", n = {}, s = {})
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ String $json;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(String str, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$json = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return BridgeService.this.new AnonymousClass1(this.$json, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            AnonymousClass1 anonymousClass1;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    ControlChannel controlChannel = BridgeService.this.controlChannel;
                    if (controlChannel != null) {
                        this.label = 1;
                        if (controlChannel.send(this.$json, this) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        anonymousClass1 = this;
                    }
                    return Unit.INSTANCE;
                case 1:
                    anonymousClass1 = this;
                    ResultKt.throwOnFailure($result);
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    private final void startListening() {
        MediaSessionManager mgr = this.msm;
        if (mgr == null) {
            return;
        }
        try {
            mgr.addOnActiveSessionsChangedListener(this.sessionsChangedListener, getListenerComponent());
            List<MediaController> activeSessions = mgr.getActiveSessions(getListenerComponent());
            if (activeSessions == null) {
                activeSessions = CollectionsKt.emptyList();
            }
            Log.i(TAG, "Initial active sessions: " + activeSessions.size());
            Iterable $this$forEach$iv = activeSessions;
            for (Object element$iv : $this$forEach$iv) {
                MediaController ctrl = (MediaController) element$iv;
                PlaybackState ps = ctrl.getPlaybackState();
                Log.i(TAG, "  - " + ctrl.getPackageName() + " (state=" + (ps != null ? Integer.valueOf(ps.getState()) : null) + ")");
            }
            onSessionsChanged(activeSessions);
        } catch (SecurityException e) {
            Log.w(TAG, "No notification access — can't read active sessions", e);
            BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new AnonymousClass2(null), 3, null);
        }
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$startListening$2, reason: invalid class name */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$startListening$2", f = "BridgeService.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;

        AnonymousClass2(Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return BridgeService.this.new AnonymousClass2(continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass2) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    intent.addFlags(268435456);
                    BridgeService.this.startActivity(intent);
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    private final void stopListening() {
        MediaSessionManager mediaSessionManager = this.msm;
        if (mediaSessionManager != null) {
            mediaSessionManager.removeOnActiveSessionsChangedListener(this.sessionsChangedListener);
        }
    }

    private final void onSessionsChanged(List<MediaController> sessions) {
        Object maxElem$iv;
        if (sessions.isEmpty()) {
            Log.d(TAG, "No active media sessions");
            return;
        }
        System.currentTimeMillis();
        List<MediaController> $this$maxByOrNull$iv = sessions;
        Iterator iterator$iv = $this$maxByOrNull$iv.iterator();
        if (iterator$iv.hasNext()) {
            maxElem$iv = iterator$iv.next();
            if (iterator$iv.hasNext()) {
                MediaController ctrl = (MediaController) maxElem$iv;
                PlaybackState ps = ctrl.getPlaybackState();
                int i = 3;
                boolean isPlaying = ps != null && ps.getState() == 3;
                boolean isBuffering = ps != null && ps.getState() == 6;
                long weight = isPlaying ? 4611686018427387903L : isBuffering ? 2305843009213693951L : 0L;
                long lastUpdate = ps != null ? ps.getLastPositionUpdateTime() : 0L;
                long maxValue$iv = weight + lastUpdate;
                while (true) {
                    Object e$iv = iterator$iv.next();
                    MediaController ctrl2 = (MediaController) e$iv;
                    PlaybackState ps2 = ctrl2.getPlaybackState();
                    boolean isPlaying2 = ps2 != null && ps2.getState() == i;
                    boolean isBuffering2 = ps2 != null && ps2.getState() == 6;
                    long weight2 = isPlaying2 ? 4611686018427387903L : isBuffering2 ? 2305843009213693951L : 0L;
                    long lastUpdate2 = ps2 != null ? ps2.getLastPositionUpdateTime() : 0L;
                    long weight3 = weight2 + lastUpdate2;
                    if (maxValue$iv < weight3) {
                        maxElem$iv = e$iv;
                        maxValue$iv = weight3;
                    }
                    if (!iterator$iv.hasNext()) {
                        break;
                    } else {
                        i = 3;
                    }
                }
            }
        } else {
            maxElem$iv = null;
        }
        MediaController best = (MediaController) maxElem$iv;
        if (best == null || best == this.currentController) {
            return;
        }
        Log.i(TAG, "Switching to session: " + best.getPackageName());
        MediaController mediaController = this.currentController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.controllerCallback);
        }
        this.currentController = best;
        best.registerCallback(this.controllerCallback);
        MediaMetadata meta = best.getMetadata();
        if (meta != null) {
            String packageName = best.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName, "getPackageName(...)");
            PlaybackState playbackState = best.getPlaybackState();
            sendTrack(meta, packageName, playbackState != null ? Integer.valueOf(playbackState.getState()) : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0098 A[Catch: Exception -> 0x00b1, TryCatch #0 {Exception -> 0x00b1, blocks: (B:15:0x004e, B:16:0x0056, B:34:0x008e, B:36:0x0098, B:38:0x00a8, B:18:0x005a, B:21:0x0063, B:22:0x0067, B:25:0x0070, B:26:0x0074, B:29:0x007d, B:30:0x0081, B:33:0x008a), top: B:45:0x004e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void handleCommand(com.frepidation.a2wmc.transport.MetadataProtocol.ControlCommand r10) {
        /*
            Method dump skipped, instruction units count: 206
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.service.BridgeService.handleCommand(com.frepidation.a2wmc.transport.MetadataProtocol$ControlCommand):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void sendTrack(MediaMetadata meta, String pkg, Integer playbackState) {
        TrackInfo.PlayState state;
        try {
            int currentVol = getMediaVolume();
            int currentMaxVol = getMaxMediaVolume();
            Log.d(TAG, "sendTrack: title=" + meta.getString("android.media.metadata.TITLE") + " vol=" + currentVol + "/" + currentMaxVol);
            TrackInfo it = extractTrackInfo(meta);
            if (playbackState != null && playbackState.intValue() == 3) {
                state = TrackInfo.PlayState.PLAYING;
            } else if (playbackState != null && playbackState.intValue() == 2) {
                state = TrackInfo.PlayState.PAUSED;
            } else if (playbackState != null && playbackState.intValue() == 6) {
                state = TrackInfo.PlayState.PLAYING;
            } else if (playbackState != null && playbackState.intValue() == 1) {
                state = TrackInfo.PlayState.STOPPED;
            } else {
                state = (playbackState != null && playbackState.intValue() == 0) ? TrackInfo.PlayState.STOPPED : TrackInfo.PlayState.STOPPED;
            }
            TrackInfo info = it.copy((255 & 1) != 0 ? it.title : null, (255 & 2) != 0 ? it.artist : null, (255 & 4) != 0 ? it.album : null, (255 & 8) != 0 ? it.duration : 0L, (255 & 16) != 0 ? it.position : 0L, (255 & 32) != 0 ? it.state : state, (255 & 64) != 0 ? it.coverBitmap : null, (255 & 128) != 0 ? it.packageName : pkg, (255 & 256) != 0 ? it.volume : currentVol >= 0 ? currentVol : it.getVolume(), (255 & 512) != 0 ? it.maxVolume : currentMaxVol);
            if (Intrinsics.areEqual(info, this.lastSent)) {
                return;
            }
            this.lastSent = info;
            sendJson(info);
        } catch (Exception e) {
            Log.e(TAG, "sendTrack error", e);
        }
    }

    private final void sendJson(TrackInfo info) throws JSONException {
        String json = MetadataProtocol.INSTANCE.serializeTrackInfo(info);
        this.lastSentJson = json;
        Log.d(TAG, "Serialized: " + StringsKt.take(json, 150) + "...");
        BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new C00261(json, null), 3, null);
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$sendJson$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$sendJson$1", f = "BridgeService.kt", i = {}, l = {414}, m = "invokeSuspend", n = {}, s = {})
    static final class C00261 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ String $json;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C00261(String str, Continuation<? super C00261> continuation) {
            super(2, continuation);
            this.$json = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return BridgeService.this.new C00261(this.$json, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C00261) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            Exception e;
            C00261 c00261;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    try {
                        ControlChannel controlChannel = BridgeService.this.controlChannel;
                        if (controlChannel != null) {
                            this.label = 1;
                            if (controlChannel.send(this.$json, this) == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            c00261 = this;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        c00261 = this;
                        Log.e(BridgeService.TAG, "send failed", e);
                    }
                    return Unit.INSTANCE;
                case 1:
                    c00261 = this;
                    try {
                        ResultKt.throwOnFailure($result);
                    } catch (Exception e3) {
                        e = e3;
                        Log.e(BridgeService.TAG, "send failed", e);
                    }
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$startHeartbeat$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$startHeartbeat$1", f = "BridgeService.kt", i = {0, 1}, l = {426, 433}, m = "invokeSuspend", n = {"$this$launch", "$this$launch"}, s = {"L$0", "L$0"})
    static final class C00281 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        int label;

        C00281(Continuation<? super C00281> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00281 c00281 = BridgeService.this.new C00281(continuation);
            c00281.L$0 = obj;
            return c00281;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C00281) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:11:0x003c A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0047  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0074  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:15:0x0052 -> B:9:0x002b). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:17:0x0061 -> B:9:0x002b). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x006f -> B:9:0x002b). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.lang.Object invokeSuspend(java.lang.Object r8) throws java.lang.Throwable {
            /*
                r7 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r7.label
                switch(r1) {
                    case 0: goto L23;
                    case 1: goto L1a;
                    case 2: goto L11;
                    default: goto L9;
                }
            L9:
                java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r8.<init>(r0)
                throw r8
            L11:
                r1 = r7
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.CoroutineScope r2 = (kotlinx.coroutines.CoroutineScope) r2
                kotlin.ResultKt.throwOnFailure(r8)
                goto L72
            L1a:
                r1 = r7
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.CoroutineScope r2 = (kotlinx.coroutines.CoroutineScope) r2
                kotlin.ResultKt.throwOnFailure(r8)
                goto L3d
            L23:
                kotlin.ResultKt.throwOnFailure(r8)
                r1 = r7
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.CoroutineScope r2 = (kotlinx.coroutines.CoroutineScope) r2
            L2b:
                r3 = r1
                kotlin.coroutines.Continuation r3 = (kotlin.coroutines.Continuation) r3
                r1.L$0 = r2
                r4 = 1
                r1.label = r4
                r4 = 10000(0x2710, double:4.9407E-320)
                java.lang.Object r3 = kotlinx.coroutines.DelayKt.delay(r4, r3)
                if (r3 != r0) goto L3d
                return r0
            L3d:
                kotlin.coroutines.CoroutineContext r3 = r2.getCoroutineContext()
                boolean r3 = kotlinx.coroutines.JobKt.isActive(r3)
                if (r3 == 0) goto L74
                com.frepidation.a2wmc.service.BridgeService r3 = com.frepidation.a2wmc.service.BridgeService.this
                com.frepidation.a2wmc.service.BridgeService.access$broadcastVolume(r3)
                com.frepidation.a2wmc.service.BridgeService r3 = com.frepidation.a2wmc.service.BridgeService.this
                java.lang.String r3 = com.frepidation.a2wmc.service.BridgeService.access$getLastSentJson$p(r3)
                if (r3 == 0) goto L2b
                java.lang.String r4 = "BridgeService"
                java.lang.String r5 = "Heartbeat: resending metadata"
                android.util.Log.d(r4, r5)
                com.frepidation.a2wmc.service.BridgeService r4 = com.frepidation.a2wmc.service.BridgeService.this
                com.frepidation.a2wmc.transport.ControlChannel r4 = com.frepidation.a2wmc.service.BridgeService.access$getControlChannel$p(r4)
                if (r4 == 0) goto L73
                r5 = r1
                kotlin.coroutines.Continuation r5 = (kotlin.coroutines.Continuation) r5
                r1.L$0 = r2
                r6 = 2
                r1.label = r6
                java.lang.Object r3 = r4.send(r3, r5)
                if (r3 != r0) goto L72
                return r0
            L72:
                goto L2b
            L73:
                goto L2b
            L74:
                kotlin.Unit r0 = kotlin.Unit.INSTANCE
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.service.BridgeService.C00281.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    private final void startHeartbeat() {
        this.heartbeatJob = BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new C00281(null), 3, null);
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.service.BridgeService$startVolumeSync$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: BridgeService.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.service.BridgeService$startVolumeSync$1", f = "BridgeService.kt", i = {0}, l = {442}, m = "invokeSuspend", n = {"$this$launch"}, s = {"L$0"})
    static final class C00291 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        int label;

        C00291(Continuation<? super C00291> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00291 c00291 = BridgeService.this.new C00291(continuation);
            c00291.L$0 = obj;
            return c00291;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C00291) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0034 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:13:0x003f  */
        /* JADX WARN: Removed duplicated region for block: B:14:0x004c  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:9:0x0032 -> B:11:0x0035). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.lang.Object invokeSuspend(java.lang.Object r7) throws java.lang.Throwable {
            /*
                r6 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r6.label
                switch(r1) {
                    case 0: goto L1a;
                    case 1: goto L11;
                    default: goto L9;
                }
            L9:
                java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r7.<init>(r0)
                throw r7
            L11:
                r1 = r6
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.CoroutineScope r2 = (kotlinx.coroutines.CoroutineScope) r2
                kotlin.ResultKt.throwOnFailure(r7)
                goto L35
            L1a:
                kotlin.ResultKt.throwOnFailure(r7)
                r1 = r6
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.CoroutineScope r2 = (kotlinx.coroutines.CoroutineScope) r2
            L22:
                r3 = r1
                kotlin.coroutines.Continuation r3 = (kotlin.coroutines.Continuation) r3
                r1.L$0 = r2
                r4 = 1
                r1.label = r4
                r4 = 180000(0x2bf20, double:8.8932E-319)
                java.lang.Object r3 = kotlinx.coroutines.DelayKt.delay(r4, r3)
                if (r3 != r0) goto L35
                return r0
            L35:
                kotlin.coroutines.CoroutineContext r3 = r2.getCoroutineContext()
                boolean r3 = kotlinx.coroutines.JobKt.isActive(r3)
                if (r3 == 0) goto L4c
                java.lang.String r3 = "BridgeService"
                java.lang.String r4 = "Volume sync interval triggered"
                android.util.Log.d(r3, r4)
                com.frepidation.a2wmc.service.BridgeService r3 = com.frepidation.a2wmc.service.BridgeService.this
                com.frepidation.a2wmc.service.BridgeService.access$broadcastVolume(r3)
                goto L22
            L4c:
                kotlin.Unit r0 = kotlin.Unit.INSTANCE
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.service.BridgeService.C00291.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    private final void startVolumeSync() {
        this.volumeSyncJob = BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new C00291(null), 3, null);
    }

    private final TrackInfo extractTrackInfo(MediaMetadata meta) {
        String album;
        String string = meta.getString("android.media.metadata.TITLE");
        String title = string == null ? "" : string;
        String string2 = meta.getString("android.media.metadata.ARTIST");
        String artist = string2 == null ? "" : string2;
        String string3 = meta.getString("android.media.metadata.ALBUM");
        if (string3 == null) {
            string3 = meta.getString("android.media.metadata.ALBUM_ARTIST");
        }
        if (string3 != null) {
            album = string3;
        } else {
            album = "";
        }
        long duration = meta.getLong("android.media.metadata.DURATION");
        return new TrackInfo(title, artist, album, duration, 0L, TrackInfo.PlayState.PLAYING, extractCover(meta), null, 0, 0, 912, null);
    }

    private final Bitmap extractCover(MediaMetadata meta) {
        Bitmap iconBitmap;
        try {
            MediaDescription description = meta.getDescription();
            if (description != null && (iconBitmap = description.getIconBitmap()) != null) {
                return iconBitmap;
            }
            Bitmap bitmap = meta.getBitmap("android.media.metadata.ALBUM_ART");
            if (bitmap != null) {
                return bitmap;
            }
            Bitmap bitmap2 = meta.getBitmap("android.media.metadata.ART");
            if (bitmap2 != null) {
                return bitmap2;
            }
            return meta.getBitmap("android.media.metadata.DISPLAY_ICON");
        } catch (Exception e) {
            return null;
        }
    }

    private final void createNotificationChannel() {
        try {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "A2WMC 桥接服务", 2);
            NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        } catch (Exception e) {
            Log.e(TAG, "createNotificationChannel error", e);
        }
    }

    private final Notification buildNotification(String content) {
        Notification notificationBuild = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("A2WMC").setContentText(content).setSmallIcon(R.drawable.ic_media_play).setOngoing(true).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }
}
