package org.bspb.smartbirds.pro.content;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by groupsky on 05.12.16.
 */

public class TrackingLocation {

    public long id;
    @NonNull
    @Expose
    @SerializedName("monitoringCode")
    public final String monitoringCode;
    @Expose
    @SerializedName("latitude")
    public final double latitude;
    @Expose
    @SerializedName("longitude")
    public final double longitude;
    @Nullable
    @Expose
    @SerializedName("altitude")
    public final Double altitude;
    @Expose
    @SerializedName("time")
    public final long time;

    public TrackingLocation(@NonNull String monitoringCode, long time, double latitude, double longitude, @Nullable Double altitude) {
        this.monitoringCode = monitoringCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.time = time;
    }

    public TrackingLocation(@NonNull String monitoringCode, @NonNull Location location) {
        this.monitoringCode = monitoringCode;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.hasAltitude() ? location.getAltitude() : null;
        this.time = location.getTime();
    }

    @Override
    public String toString() {
        return "TrackingLocation{" +
                "id=" + id +
                ", monitoringCode='" + monitoringCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", time=" + time +
                '}';
    }
}
