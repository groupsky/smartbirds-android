package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by groupsky on 05.10.16.
 */
public class User {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("firstName")
    @Expose
    public String firstName;

    @SerializedName("lastName")
    @Expose
    public String lastName;

    @SerializedName("isAdmin")
    @Expose
    public boolean isAdmin;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
