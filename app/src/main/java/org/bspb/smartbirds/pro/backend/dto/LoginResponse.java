package org.bspb.smartbirds.pro.backend.dto;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dani on 08.08.16.
 */
public class LoginResponse {
    @SerializedName("csrfToken")
    @Expose
    @Nullable
    public String token;

    @SerializedName("success")
    @Expose
    @Nullable
    public boolean success;

    @SerializedName("error")
    @Expose
    @Nullable
    public String error;

    @SerializedName("user")
    @Expose
    public User user;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", success=" + success +
                ", error='" + error + '\'' +
                ", user=" + user +
                '}';
    }

}
