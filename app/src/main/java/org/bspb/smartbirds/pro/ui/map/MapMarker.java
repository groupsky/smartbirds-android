package org.bspb.smartbirds.pro.ui.map;

import java.io.Serializable;

/**
 * Created by dani on 14-11-7.
 */
public class MapMarker implements Serializable {
    String title;
    double latitude;
    double longitude;

    public MapMarker(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

