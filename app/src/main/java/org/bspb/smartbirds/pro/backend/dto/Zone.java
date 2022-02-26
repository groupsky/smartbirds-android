package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.room.model.ZoneModel;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.util.List;

/**
 * Created by groupsky on 06.10.16.
 */
public class Zone {

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

    public static Zone fromDbModel(ZoneModel zone) {
        return SBGsonParser.createParser().fromJson(new String(zone.getData()), Zone.class);
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id='" + id + '\'' +
                ", coordinates=" + coordinates +
                ", locationId=" + locationId +
                '}';
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

}
