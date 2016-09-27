package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by groupsky on 27.09.16.
 */

public class ResponseEnvelope<T> {

    @Expose
    @SerializedName("data")
    public T data;

    @Override
    public String toString() {
        return "ResponseEnvelope{" +
                "data=" + data +
                '}';
    }
}
