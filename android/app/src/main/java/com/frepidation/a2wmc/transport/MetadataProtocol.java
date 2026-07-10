package com.frepidation.a2wmc.transport;

import android.graphics.Bitmap;
import android.util.Base64;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.frepidation.a2wmc.model.TrackInfo;
import java.io.ByteArrayOutputStream;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: MetadataProtocol.kt */
/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\nB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t¨\u0006\u000b"}, d2 = {"Lcom/frepidation/a2wmc/transport/MetadataProtocol;", "", "()V", "parseCommand", "Lcom/frepidation/a2wmc/transport/MetadataProtocol$ControlCommand;", "json", "", "serializeTrackInfo", "info", "Lcom/frepidation/a2wmc/model/TrackInfo;", "ControlCommand", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class MetadataProtocol {
    public static final MetadataProtocol INSTANCE = new MetadataProtocol();

    /* JADX INFO: compiled from: MetadataProtocol.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[TrackInfo.PlayState.values().length];
            try {
                iArr[TrackInfo.PlayState.PLAYING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[TrackInfo.PlayState.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[TrackInfo.PlayState.STOPPED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private MetadataProtocol() {
    }

    public final String serializeTrackInfo(TrackInfo info) throws JSONException {
        String coverB64;
        String stateStr;
        Intrinsics.checkNotNullParameter(info, "info");
        Bitmap bitmap = info.getCoverBitmap();
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            coverB64 = Base64.encodeToString(stream.toByteArray(), 2);
        } else {
            coverB64 = null;
        }
        switch (WhenMappings.$EnumSwitchMapping$0[info.getState().ordinal()]) {
            case 1:
                stateStr = "playing";
                break;
            case 2:
                stateStr = "paused";
                break;
            case 3:
                stateStr = "stopped";
                break;
            default:
                throw new NoWhenBranchMatchedException();
        }
        JSONObject obj = new JSONObject();
        obj.put("type", "metadata");
        obj.put("title", info.getTitle());
        obj.put("artist", info.getArtist());
        obj.put("album", info.getAlbum());
        obj.put("duration", info.getDuration());
        obj.put("position", info.getPosition());
        obj.put("state", stateStr);
        obj.put("package", info.getPackageName());
        obj.put("volume", info.getVolume());
        obj.put("maxVolume", info.getMaxVolume());
        if (coverB64 != null) {
            obj.put("cover", coverB64);
        }
        String string = obj.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX INFO: compiled from: MetadataProtocol.kt */
    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\f\u001a\u00020\rHÖ\u0001J\t\u0010\u000e\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u000f"}, d2 = {"Lcom/frepidation/a2wmc/transport/MetadataProtocol$ControlCommand;", "", "action", "", "(Ljava/lang/String;)V", "getAction", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final /* data */ class ControlCommand {
        private final String action;

        public static /* synthetic */ ControlCommand copy$default(ControlCommand controlCommand, String str, int i, Object obj) {
            if ((i & 1) != 0) {
                str = controlCommand.action;
            }
            return controlCommand.copy(str);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final String getAction() {
            return this.action;
        }

        public final ControlCommand copy(String action) {
            Intrinsics.checkNotNullParameter(action, "action");
            return new ControlCommand(action);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof ControlCommand) && Intrinsics.areEqual(this.action, ((ControlCommand) other).action);
        }

        public int hashCode() {
            return this.action.hashCode();
        }

        public String toString() {
            return "ControlCommand(action=" + this.action + ")";
        }

        public ControlCommand(String action) {
            Intrinsics.checkNotNullParameter(action, "action");
            this.action = action;
        }

        public final String getAction() {
            return this.action;
        }
    }

    public final ControlCommand parseCommand(String json) {
        Intrinsics.checkNotNullParameter(json, "json");
        try {
            JSONObject obj = new JSONObject(json);
            if (!Intrinsics.areEqual(obj.optString("type"), "command")) {
                return null;
            }
            String strOptString = obj.optString("action", "");
            Intrinsics.checkNotNullExpressionValue(strOptString, "optString(...)");
            return new ControlCommand(strOptString);
        } catch (Exception e) {
            return null;
        }
    }
}
