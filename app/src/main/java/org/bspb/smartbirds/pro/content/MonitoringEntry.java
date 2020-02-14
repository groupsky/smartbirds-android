package org.bspb.smartbirds.pro.content;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.HashMap;

/**
 * Created by groupsky on 05.12.16.
 */

public class MonitoringEntry implements Parcelable {

    public long id;
    @NonNull
    public final String monitoringCode;
    @NonNull
    public final EntryType type;
    @NonNull
    public HashMap<String, String> data = new HashMap<>();

    public MonitoringEntry(@NonNull String monitoringCode, @NonNull EntryType type) {
        this.monitoringCode = monitoringCode;
        this.type = type;
    }

    @Override
    public String toString() {
        return "MonitoringEntry{" +
                "id=" + id +
                ", monitoringCode='" + monitoringCode + '\'' +
                ", type=" + type +
                ", data=" + data +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.monitoringCode);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeSerializable(this.data);
    }

    protected MonitoringEntry(Parcel in) {
        this.id = in.readLong();
        this.monitoringCode = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : EntryType.values()[tmpType];
        this.data = (HashMap<String, String>) in.readSerializable();
    }

    public static final Parcelable.Creator<MonitoringEntry> CREATOR = new Parcelable.Creator<MonitoringEntry>() {
        @Override
        public MonitoringEntry createFromParcel(Parcel source) {
            return new MonitoringEntry(source);
        }

        @Override
        public MonitoringEntry[] newArray(int size) {
            return new MonitoringEntry[size];
        }
    };
}
