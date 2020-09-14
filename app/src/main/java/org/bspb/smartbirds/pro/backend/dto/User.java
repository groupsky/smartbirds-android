package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("bgatlasCells")
    @Expose
    public List<BGAtlasCell> bgAtlasCells;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", isAdmin=").append(isAdmin);
        sb.append(", bgAtlasCells=").append(bgAtlasCells);
        sb.append('}');
        return sb.toString();
    }
}
