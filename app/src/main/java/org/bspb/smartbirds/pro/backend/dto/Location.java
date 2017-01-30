package org.bspb.smartbirds.pro.backend.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static org.bspb.smartbirds.pro.db.LocationColumns.DATA;
import static org.bspb.smartbirds.pro.db.LocationColumns.LAT;
import static org.bspb.smartbirds.pro.db.LocationColumns.LON;
import static org.bspb.smartbirds.pro.db.LocationColumns.NAME_BG;
import static org.bspb.smartbirds.pro.db.LocationColumns.NAME_EN;
import static org.bspb.smartbirds.pro.db.LocationColumns._ID;

/**
 * Created by groupsky on 27.01.17.
 */

public class Location {

    public static final String[] DEFAULT_PROJECTION = {
            _ID, DATA
    };

    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("latitude")
    public double latitude;

    @Expose
    @SerializedName("longitude")
    public double longitude;

    @Expose
    @SerializedName("type")
    public Label type;

    @Expose
    @SerializedName("name")
    public Label name;

    @Expose
    @SerializedName("area")
    public Label area;

    @Expose
    @SerializedName("region")
    public Label region;

    @Expose
    @SerializedName("ekatte")
    public String ekatte;

    public static Location fromCursor(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndexOrThrow(DATA));
        return new Gson().fromJson(data, Location.class);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", type=" + type +
                ", name=" + name +
                ", area=" + area +
                ", region=" + region +
                ", ekatte='" + ekatte + '\'' +
                '}';
    }

    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put(_ID, id);
        cv.put(LAT, latitude);
        cv.put(LON, longitude);
        cv.put(NAME_BG, name != null ? name.bg : null);
        cv.put(NAME_EN, name != null ? name.en : null);
        cv.put(DATA, new Gson().toJson(this));
        return cv;
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
