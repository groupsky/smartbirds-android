package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by groupsky on 27.09.16.
 */

public class ResponseListEnvelope<T> {

    @Expose
    @SerializedName("data")
    public List<T> data;
    @Expose
    @SerializedName("count")
    public int count;

    @Override
    public String toString() {
        return "ResponseListEnvelope{" +
                "data=" + data +
                '}';
    }
}
