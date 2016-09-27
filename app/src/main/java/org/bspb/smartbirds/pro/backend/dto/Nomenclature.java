package org.bspb.smartbirds.pro.backend.dto;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;

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

    transient public String localeLabel;

    public Nomenclature() {
    }

    public Nomenclature(Cursor cursor, String localeColumn) {
        type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
        label = new Label(cursor);
        localeLabel = cursor.getString(cursor.getColumnIndexOrThrow(localeColumn));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nomenclature that = (Nomenclature) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return label != null ? label.equals(that.label) : that.label == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

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

        public Label() {
        }

        public Label(Cursor cursor) {
            bg = cursor.getString(cursor.getColumnIndexOrThrow(LABEL_BG));
            en = cursor.getString(cursor.getColumnIndexOrThrow(LABEL_EN));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Label label = (Label) o;

            if (bg != null ? !bg.equals(label.bg) : label.bg != null) return false;
            return en != null ? en.equals(label.en) : label.en == null;

        }

        @Override
        public int hashCode() {
            int result = bg != null ? bg.hashCode() : 0;
            result = 31 * result + (en != null ? en.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Label{" +
                    "bg='" + bg + '\'' +
                    ", en='" + en + '\'' +
                    '}';
        }
    }
}
