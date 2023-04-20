package org.bspb.smartbirds.pro.content;

import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    @Expose
    @SerializedName("accuracy")
    public final Float accuracy;
    @Expose
    @SerializedName("verticalAccuracy")
    public final Float verticalAccuracy;
    @Expose
    @SerializedName("speed")
    public final Float speed;


    public TrackingLocation(@NonNull String monitoringCode, @NonNull Location location) {
        this.monitoringCode = monitoringCode;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.hasAltitude() ? location.getAltitude() : null;
        this.time = location.getTime();
        this.accuracy = location.hasAccuracy() ? location.getAccuracy() : null;
        this.verticalAccuracy = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasVerticalAccuracy() ? location.getVerticalAccuracyMeters() : null;
        this.speed = location.hasSpeed() ? location.getSpeed() : null;
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
                ", accuracy=" + accuracy +
                ", verticalAccuracy=" + verticalAccuracy +
                ", speed=" + speed +
                '}';
    }
}
