package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dani on 08.08.16.
 */
public class LoginRequest {
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("gdprConsent")
    @Expose
    public Boolean gdprConsent;

    public LoginRequest(String email, String password, Boolean gdprConsent) {
        this.email = email;
        this.password = password;
        this.gdprConsent = gdprConsent;
    }

    public LoginRequest(String email, String password) {
        this(email, password, null);
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gdprConsent='" + gdprConsent + '\'' +
                '}';
    }
}
