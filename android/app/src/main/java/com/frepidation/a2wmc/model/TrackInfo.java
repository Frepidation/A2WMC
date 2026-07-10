package com.frepidation.a2wmc.model;

import android.graphics.Bitmap;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: TrackInfo.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u001d\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001:\u00011Bk\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f¢\u0006\u0002\u0010\u0011J\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u000fHÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0007HÆ\u0003J\t\u0010&\u001a\u00020\u0007HÆ\u0003J\t\u0010'\u001a\u00020\nHÆ\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\fHÆ\u0003J\t\u0010)\u001a\u00020\u0003HÆ\u0003J\t\u0010*\u001a\u00020\u000fHÆ\u0003Jo\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000fHÆ\u0001J\u0013\u0010,\u001a\u00020-2\b\u0010.\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010/\u001a\u00020\u000fHÖ\u0001J\t\u00100\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0010\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\r\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0018R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0013R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001a¨\u00062"}, d2 = {"Lcom/frepidation/a2wmc/model/TrackInfo;", "", "title", "", "artist", "album", "duration", "", "position", "state", "Lcom/frepidation/a2wmc/model/TrackInfo$PlayState;", "coverBitmap", "Landroid/graphics/Bitmap;", "packageName", "volume", "", "maxVolume", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJLcom/frepidation/a2wmc/model/TrackInfo$PlayState;Landroid/graphics/Bitmap;Ljava/lang/String;II)V", "getAlbum", "()Ljava/lang/String;", "getArtist", "getCoverBitmap", "()Landroid/graphics/Bitmap;", "getDuration", "()J", "getMaxVolume", "()I", "getPackageName", "getPosition", "getState", "()Lcom/frepidation/a2wmc/model/TrackInfo$PlayState;", "getTitle", "getVolume", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "PlayState", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class TrackInfo {
    private final String album;
    private final String artist;
    private final Bitmap coverBitmap;
    private final long duration;
    private final int maxVolume;
    private final String packageName;
    private final long position;
    private final PlayState state;
    private final String title;
    private final int volume;

    public TrackInfo() {
        this(null, null, null, 0L, 0L, null, null, null, 0, 0, 1023, null);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getTitle() {
        return this.title;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final int getMaxVolume() {
        return this.maxVolume;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getArtist() {
        return this.artist;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getAlbum() {
        return this.album;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final long getDuration() {
        return this.duration;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final long getPosition() {
        return this.position;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final PlayState getState() {
        return this.state;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final Bitmap getCoverBitmap() {
        return this.coverBitmap;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final String getPackageName() {
        return this.packageName;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final int getVolume() {
        return this.volume;
    }

    public final TrackInfo copy(String title, String artist, String album, long duration, long position, PlayState state, Bitmap coverBitmap, String packageName, int volume, int maxVolume) {
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(artist, "artist");
        Intrinsics.checkNotNullParameter(album, "album");
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        return new TrackInfo(title, artist, album, duration, position, state, coverBitmap, packageName, volume, maxVolume);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TrackInfo)) {
            return false;
        }
        TrackInfo trackInfo = (TrackInfo) other;
        return Intrinsics.areEqual(this.title, trackInfo.title) && Intrinsics.areEqual(this.artist, trackInfo.artist) && Intrinsics.areEqual(this.album, trackInfo.album) && this.duration == trackInfo.duration && this.position == trackInfo.position && this.state == trackInfo.state && Intrinsics.areEqual(this.coverBitmap, trackInfo.coverBitmap) && Intrinsics.areEqual(this.packageName, trackInfo.packageName) && this.volume == trackInfo.volume && this.maxVolume == trackInfo.maxVolume;
    }

    public int hashCode() {
        int iHashCode = ((((((((((this.title.hashCode() * 31) + this.artist.hashCode()) * 31) + this.album.hashCode()) * 31) + Long.hashCode(this.duration)) * 31) + Long.hashCode(this.position)) * 31) + this.state.hashCode()) * 31;
        Bitmap bitmap = this.coverBitmap;
        return ((((((iHashCode + (bitmap == null ? 0 : bitmap.hashCode())) * 31) + this.packageName.hashCode()) * 31) + Integer.hashCode(this.volume)) * 31) + Integer.hashCode(this.maxVolume);
    }

    public String toString() {
        return "TrackInfo(title=" + this.title + ", artist=" + this.artist + ", album=" + this.album + ", duration=" + this.duration + ", position=" + this.position + ", state=" + this.state + ", coverBitmap=" + this.coverBitmap + ", packageName=" + this.packageName + ", volume=" + this.volume + ", maxVolume=" + this.maxVolume + ")";
    }

    public TrackInfo(String title, String artist, String album, long duration, long position, PlayState state, Bitmap coverBitmap, String packageName, int volume, int maxVolume) {
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(artist, "artist");
        Intrinsics.checkNotNullParameter(album, "album");
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.position = position;
        this.state = state;
        this.coverBitmap = coverBitmap;
        this.packageName = packageName;
        this.volume = volume;
        this.maxVolume = maxVolume;
    }

    public /* synthetic */ TrackInfo(String str, String str2, String str3, long j, long j2, PlayState playState, Bitmap bitmap, String str4, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? "" : str, (i3 & 2) != 0 ? "" : str2, (i3 & 4) != 0 ? "" : str3, (i3 & 8) != 0 ? 0L : j, (i3 & 16) == 0 ? j2 : 0L, (i3 & 32) != 0 ? PlayState.STOPPED : playState, (i3 & 64) != 0 ? null : bitmap, (i3 & 128) == 0 ? str4 : "", (i3 & 256) != 0 ? -1 : i, (i3 & 512) != 0 ? 15 : i2);
    }

    public final String getTitle() {
        return this.title;
    }

    public final String getArtist() {
        return this.artist;
    }

    public final String getAlbum() {
        return this.album;
    }

    public final long getDuration() {
        return this.duration;
    }

    public final long getPosition() {
        return this.position;
    }

    public final PlayState getState() {
        return this.state;
    }

    public final Bitmap getCoverBitmap() {
        return this.coverBitmap;
    }

    public final String getPackageName() {
        return this.packageName;
    }

    public final int getVolume() {
        return this.volume;
    }

    public final int getMaxVolume() {
        return this.maxVolume;
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* JADX INFO: compiled from: TrackInfo.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"}, d2 = {"Lcom/frepidation/a2wmc/model/TrackInfo$PlayState;", "", "(Ljava/lang/String;I)V", "PLAYING", "PAUSED", "STOPPED", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class PlayState {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ PlayState[] $VALUES;
        public static final PlayState PLAYING = new PlayState("PLAYING", 0);
        public static final PlayState PAUSED = new PlayState("PAUSED", 1);
        public static final PlayState STOPPED = new PlayState("STOPPED", 2);

        private static final /* synthetic */ PlayState[] $values() {
            return new PlayState[]{PLAYING, PAUSED, STOPPED};
        }

        public static EnumEntries<PlayState> getEntries() {
            return $ENTRIES;
        }

        public static PlayState valueOf(String str) {
            return (PlayState) Enum.valueOf(PlayState.class, str);
        }

        public static PlayState[] values() {
            return (PlayState[]) $VALUES.clone();
        }

        static {
            PlayState[] playStateArr$values = $values();
            $VALUES = playStateArr$values;
            $ENTRIES = EnumEntriesKt.enumEntries(playStateArr$values);
        }

        private PlayState(String $enum$name, int $enum$ordinal) {
        }
    }
}
