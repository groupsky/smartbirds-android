package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by groupsky on 28.09.16.
 */
public class FileId {

    @Expose
    @SerializedName("id")
    public String id;

    @Override
    public String toString() {
        return "FileId{" +
                "id='" + id + '\'' +
                '}';
    }
}
