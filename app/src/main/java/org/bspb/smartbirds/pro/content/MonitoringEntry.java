package org.bspb.smartbirds.pro.content;

import android.support.annotation.NonNull;

import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by groupsky on 05.12.16.
 */

public class MonitoringEntry {

    public long id;
    @NonNull
    public final String monitoringCode;
    @NonNull
    public final EntryType type;
    @NonNull
    public final Map<String, String> data = new HashMap<>();

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
}
