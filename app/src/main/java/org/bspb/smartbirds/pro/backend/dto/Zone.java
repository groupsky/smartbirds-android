package org.bspb.smartbirds.pro.backend.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.db.ZoneColumns;

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

    public static Zone fromCursor(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndexOrThrow(ZoneColumns.DATA));
        return new Gson().fromJson(data, Zone.class);
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
        cv.put(ZoneColumns.LOCATION_ID, locationId);
        cv.put(ZoneColumns.DATA, new Gson().toJson(this));
        return cv;
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
