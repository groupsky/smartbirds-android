package org.bspb.smartbirds.pro.ui.map;

import java.io.Serializable;

/**
 * Created by dani on 14-11-7.
 */
public class MapMarker implements Serializable {
    private final String title;
    private final double latitude;
    private final double longitude;

    public MapMarker(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapMarker mapMarker = (MapMarker) o;

        if (Double.compare(mapMarker.latitude, latitude) != 0) return false;
        if (Double.compare(mapMarker.longitude, longitude) != 0) return false;
        return title != null ? title.equals(mapMarker.title) : mapMarker.title == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = title != null ? title.hashCode() : 0;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

