package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by groupsky on 27.09.16.
 */
public class Nomenclature {

    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("label")
    public Label label;

    @Override
    public String toString() {
        return "Nomenclature{" +
                "type='" + type + '\'' +
                ", label=" + label +
                '}';
    }

    public static class Label {

        @Expose
        @SerializedName("bg")
        public String bg;

        @Expose
        @SerializedName("en")
        public String en;

        @Override
        public String toString() {
            return "Label{" +
                    "bg='" + bg + '\'' +
                    ", en='" + en + '\'' +
                    '}';
        }
    }
}
