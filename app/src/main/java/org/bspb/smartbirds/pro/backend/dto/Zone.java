package org.bspb.smartbirds.pro.backend.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.db.ZoneColumns;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.util.List;

/**
 * Created by groupsky on 06.10.16.
 */
public class Zone {

    public static final String[] DEFAULT_PROJECTION = {
            ZoneColumns._ID,
            ZoneColumns.DATA
    };

    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("coordinates")
    public List<Coordinate> coordinates;

    @Expose
    @SerializedName("locationId")
    public long locationId;

    private transient Coordinate center;

    public static Zone fromCursor(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndexOrThrow(ZoneColumns.DATA));
        return SBGsonParser.createParser().fromJson(data, Zone.class);
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id='" + id + '\'' +
                ", coordinates=" + coordinates +
                ", locationId=" + locationId +
                '}';
    }

    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put(ZoneColumns._ID, id);
        cv.put(ZoneColumns.LOCATION_ID, locationId);
        cv.put(ZoneColumns.DATA, SBGsonParser.createParser().toJson(this));
        return cv;
    }

    public Coordinate getCenter() {
        if (center != null) return center;
        center = new Coordinate();
        int cnt = 0;
        for (Coordinate c : coordinates) {
            cnt++;
            center.latitude += c.latitude;
            center.longitude += c.longitude;
        }
        if (cnt > 0) {
            center.latitude /= cnt;
            center.longitude /= cnt;
        }
        return center;
    }

    public static class Coordinate {

        @Expose
        @SerializedName("latitude")
        public double latitude;

        @Expose
        @SerializedName("longitude")
        public double longitude;

        @Override
        public String toString() {
            return "Coordinate{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
