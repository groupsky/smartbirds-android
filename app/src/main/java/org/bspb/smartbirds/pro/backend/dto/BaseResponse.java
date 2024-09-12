package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @Expose
    @SerializedName("error")
    public String error;

    @Expose
    @SerializedName("success")
    public boolean success;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseResponse{");
        sb.append("error='").append(error).append('\'');
        sb.append(", success=").append(success);
        sb.append('}');
        return sb.toString();
    }
}
