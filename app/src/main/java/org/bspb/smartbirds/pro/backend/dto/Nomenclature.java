package org.bspb.smartbirds.pro.backend.dto;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.db.NomenclatureColumns;

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

    @Expose(serialize = false, deserialize = false)
    public String localeLabel;

    public Nomenclature() {
    }

    public Nomenclature(Cursor cursor, String localeColumn) {
        type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
        label = new Label(cursor);
        localeLabel = cursor.getString(cursor.getColumnIndexOrThrow(localeColumn));
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
        public String toString() {
            return "Label{" +
                    "bg='" + bg + '\'' +
                    ", en='" + en + '\'' +
                    '}';
        }
    }
}
