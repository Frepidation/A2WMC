package com.frepidation.a2wmc.transport;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.frepidation.a2wmc.transport.MetadataProtocol;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.channels.Channel;
import kotlinx.coroutines.channels.ChannelKt;

/* JADX INFO: compiled from: ControlChannel.kt */
/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 !2\u00020\u0001:\u0001!B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007¢\u0006\u0002\u0010\nJ\b\u0010\u0015\u001a\u00020\tH\u0002J\b\u0010\u0016\u001a\u00020\tH\u0002J\b\u0010\u0017\u001a\u00020\tH\u0002J\u0016\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u0003H\u0086@¢\u0006\u0002\u0010\u001aJ\u000e\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001dJ\u0006\u0010\u001e\u001a\u00020\tJ\u000e\u0010\u001f\u001a\u00020\tH\u0082@¢\u0006\u0002\u0010 R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/frepidation/a2wmc/transport/ControlChannel;", "", "host", "", "port", "", "onCommand", "Lkotlin/Function1;", "Lcom/frepidation/a2wmc/transport/MetadataProtocol$ControlCommand;", "", "(Ljava/lang/String;ILkotlin/jvm/functions/Function1;)V", "job", "Lkotlinx/coroutines/Job;", "reader", "Ljava/io/BufferedReader;", "sendQueue", "Lkotlinx/coroutines/channels/Channel;", "socket", "Ljava/net/Socket;", "writer", "Ljava/io/OutputStreamWriter;", "connect", "disconnect", "readLoop", "send", "message", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "start", "scope", "Lkotlinx/coroutines/CoroutineScope;", "stop", "writeLoop", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class ControlChannel {
    private static final long RECONNECT_DELAY = 3000;
    private static final String TAG = "ControlChannel";
    private final String host;
    private Job job;
    private final Function1<MetadataProtocol.ControlCommand, Unit> onCommand;
    private final int port;
    private BufferedReader reader;
    private final Channel<String> sendQueue;
    private Socket socket;
    private OutputStreamWriter writer;

    /* JADX INFO: renamed from: com.frepidation.a2wmc.transport.ControlChannel$writeLoop$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: ControlChannel.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.transport.ControlChannel", f = "ControlChannel.kt", i = {0}, l = {105}, m = "writeLoop", n = {"this"}, s = {"L$0"})
    static final class C00301 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00301(Continuation<? super C00301> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ControlChannel.this.writeLoop(this);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ControlChannel(String host, int port, Function1<? super MetadataProtocol.ControlCommand, Unit> onCommand) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(onCommand, "onCommand");
        this.host = host;
        this.port = port;
        this.onCommand = onCommand;
        this.sendQueue = ChannelKt.Channel$default(-2, null, null, 6, null);
    }

    /* JADX INFO: renamed from: com.frepidation.a2wmc.transport.ControlChannel$start$1, reason: invalid class name */
    /* JADX INFO: compiled from: ControlChannel.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.frepidation.a2wmc.transport.ControlChannel$start$1", f = "ControlChannel.kt", i = {0, 0, 1, 2}, l = {45, 46, 54}, m = "invokeSuspend", n = {"$this$launch", "writeJob", "$this$launch", "$this$launch"}, s = {"L$0", "L$1", "L$0", "L$0"})
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ CoroutineScope $scope;
        private /* synthetic */ Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(CoroutineScope coroutineScope, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$scope = coroutineScope;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass1 anonymousClass1 = ControlChannel.this.new AnonymousClass1(this.$scope, continuation);
            anonymousClass1.L$0 = obj;
            return anonymousClass1;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x0051 A[Catch: Exception -> 0x0028, CancellationException -> 0x003f, TRY_ENTER, TRY_LEAVE, TryCatch #0 {CancellationException -> 0x003f, blocks: (B:22:0x0051, B:26:0x00c7, B:8:0x0023, B:13:0x0034), top: B:41:0x0009 }] */
        /* JADX WARN: Removed duplicated region for block: B:28:0x00d7 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:29:0x00d8  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x0105  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x011b  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:33:0x0103 -> B:20:0x004a). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:35:0x0115 -> B:20:0x004a). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.lang.Object invokeSuspend(java.lang.Object r14) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 298
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.transport.ControlChannel.AnonymousClass1.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public final void start(CoroutineScope scope) {
        Intrinsics.checkNotNullParameter(scope, "scope");
        this.job = BuildersKt__Builders_commonKt.launch$default(scope, Dispatchers.getIO(), null, new AnonymousClass1(scope, null), 2, null);
    }

    public final void stop() {
        Job job = this.job;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        disconnect();
    }

    public final Object send(String message, Continuation<? super Unit> continuation) {
        Object objSend = this.sendQueue.send(message, continuation);
        return objSend == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objSend : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final synchronized void connect() {
        Socket socket = this.socket;
        boolean z = false;
        if (socket != null && socket.isConnected()) {
            z = true;
        }
        if (z) {
            return;
        }
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(this.host, this.port), 5000);
        sock.setSoTimeout(30000);
        this.socket = sock;
        this.writer = new OutputStreamWriter(sock.getOutputStream(), Charsets.UTF_8);
        this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), Charsets.UTF_8));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final synchronized void disconnect() {
        try {
            BufferedReader bufferedReader = this.reader;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            OutputStreamWriter outputStreamWriter = this.writer;
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            Socket socket = this.socket;
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        }
        this.reader = null;
        this.writer = null;
        this.socket = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void readLoop() {
        MetadataProtocol.ControlCommand cmd;
        while (true) {
            try {
                BufferedReader bufferedReader = this.reader;
                String line = bufferedReader != null ? bufferedReader.readLine() : null;
                String it = line;
                if (line != null) {
                    MetadataProtocol metadataProtocol = MetadataProtocol.INSTANCE;
                    if (it != null && (cmd = metadataProtocol.parseCommand(it)) != null) {
                        this.onCommand.invoke(cmd);
                    }
                } else {
                    return;
                }
            } catch (Exception e) {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0058 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0067 A[Catch: Exception -> 0x0084, TryCatch #1 {Exception -> 0x0084, blocks: (B:21:0x005f, B:23:0x0067, B:25:0x0071), top: B:35:0x005f }] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:20:0x0059 -> B:35:0x005f). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object writeLoop(kotlin.coroutines.Continuation<? super kotlin.Unit> r9) throws java.lang.Throwable {
        /*
            r8 = this;
            boolean r0 = r9 instanceof com.frepidation.a2wmc.transport.ControlChannel.C00301
            if (r0 == 0) goto L14
            r0 = r9
            com.frepidation.a2wmc.transport.ControlChannel$writeLoop$1 r0 = (com.frepidation.a2wmc.transport.ControlChannel.C00301) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r9 = r0.label
            int r9 = r9 - r2
            r0.label = r9
            goto L19
        L14:
            com.frepidation.a2wmc.transport.ControlChannel$writeLoop$1 r0 = new com.frepidation.a2wmc.transport.ControlChannel$writeLoop$1
            r0.<init>(r9)
        L19:
            r9 = r0
            java.lang.Object r0 = r9.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r9.label
            switch(r2) {
                case 0: goto L3d;
                case 1: goto L2d;
                default: goto L25;
            }
        L25:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
            java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
            r9.<init>(r0)
            throw r9
        L2d:
            java.lang.Object r2 = r9.L$1
            kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
            java.lang.Object r3 = r9.L$0
            com.frepidation.a2wmc.transport.ControlChannel r3 = (com.frepidation.a2wmc.transport.ControlChannel) r3
            kotlin.ResultKt.throwOnFailure(r0)     // Catch: java.lang.Exception -> L87
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r0
            goto L5f
        L3d:
            kotlin.ResultKt.throwOnFailure(r0)
            r2 = r8
            kotlinx.coroutines.channels.Channel<java.lang.String> r3 = r2.sendQueue     // Catch: java.lang.Exception -> L87
            kotlinx.coroutines.channels.ChannelIterator r3 = r3.iterator()     // Catch: java.lang.Exception -> L87
            r7 = r3
            r3 = r2
            r2 = r7
        L4b:
            r9.L$0 = r3     // Catch: java.lang.Exception -> L87
            r9.L$1 = r2     // Catch: java.lang.Exception -> L87
            r4 = 1
            r9.label = r4     // Catch: java.lang.Exception -> L87
            java.lang.Object r4 = r2.hasNext(r9)     // Catch: java.lang.Exception -> L87
            if (r4 != r1) goto L59
            return r1
        L59:
            r7 = r1
            r1 = r0
            r0 = r4
            r4 = r3
            r3 = r2
            r2 = r7
        L5f:
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch: java.lang.Exception -> L84
            boolean r0 = r0.booleanValue()     // Catch: java.lang.Exception -> L84
            if (r0 == 0) goto L89
            java.lang.Object r0 = r3.next()     // Catch: java.lang.Exception -> L84
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Exception -> L84
            java.io.OutputStreamWriter r5 = r4.writer     // Catch: java.lang.Exception -> L84
            if (r5 == 0) goto L7f
            r6 = 0
            r5.write(r0)     // Catch: java.lang.Exception -> L84
            r0 = 10
            r5.write(r0)     // Catch: java.lang.Exception -> L84
            r5.flush()     // Catch: java.lang.Exception -> L84
        L7f:
            r0 = r1
            r1 = r2
            r2 = r3
            r3 = r4
            goto L4b
        L84:
            r0 = move-exception
            r0 = r1
            goto L88
        L87:
            r1 = move-exception
        L88:
            r1 = r0
        L89:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.frepidation.a2wmc.transport.ControlChannel.writeLoop(kotlin.coroutines.Continuation):java.lang.Object");
    }
}
