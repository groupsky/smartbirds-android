package org.bspb.smartbirds.pro.content;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.R;

import java.util.HashMap;

import static org.bspb.smartbirds.pro.content.Monitoring.Status.wip;

/**
 * Created by groupsky on 05.12.16.
 */

public class Monitoring {

    public enum Status {
        wip("wip", R.string.filter_monitoring_status_wip),
        finished("up", R.string.filter_monitoring_status_finished),
        uploaded(null, R.string.filter_monitoring_status_uploaded),
        canceled("cancel", R.string.filter_monitoring_status_canceled),
        paused("paused", R.string.filter_monitoring_status_paused);

        public final String legacySuffix;

        public final int label;

        Status(String legacySuffix, int labelId) {
            this.legacySuffix = legacySuffix;
            label = labelId;
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

    public int entriesCount = 0;

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
                ", entriesCount=" + entriesCount +
                '}';
    }
}
