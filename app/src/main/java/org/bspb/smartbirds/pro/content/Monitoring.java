package org.bspb.smartbirds.pro.content;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import static org.bspb.smartbirds.pro.content.Monitoring.Status.wip;

/**
 * Created by groupsky on 05.12.16.
 */

public class Monitoring {

    public enum Status {
        wip ("wip"), finished("up"), uploaded(null), canceled("cancel"), paused("paused");

        public final String legacySuffix;

        Status(String legacySuffix) {
            this.legacySuffix = legacySuffix;
        }
    }

    public long id;
    @NonNull
    @Expose
    @SerializedName("code")
    public final String code;
    @Expose
    @SerializedName("status")
    public Status status = wip;
    @Expose
    @SerializedName("commonForm")
    public final HashMap<String, String> commonForm = new HashMap<>();
    @Expose
    @SerializedName("pictureCounter")
    public int pictureCounter = 0;

    Monitoring(@NonNull String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Monitoring{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", commonForm=" + commonForm +
                ", pictureCounter=" + pictureCounter +
                '}';
    }
}
