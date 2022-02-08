package org.bspb.smartbirds.pro.content;

import static org.bspb.smartbirds.pro.content.Monitoring.Status.wip;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.R;

import java.util.HashMap;

/**
 * Created by groupsky on 05.12.16.
 */

public class Monitoring {

    public enum Status {
        wip(R.string.filter_monitoring_status_wip),
        finished(R.string.filter_monitoring_status_finished),
        uploaded(R.string.filter_monitoring_status_uploaded),
        canceled(R.string.filter_monitoring_status_canceled),
        paused(R.string.filter_monitoring_status_paused);

        public final int label;

        Status(int labelId) {
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

    public Monitoring(@NonNull String code) {
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
