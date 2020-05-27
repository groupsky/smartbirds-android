package org.bspb.smartbirds.pro.ui.map;

import org.bspb.smartbirds.pro.enums.EntryType;

import java.io.Serializable;

/**
 * Created by dani on 14-11-7.
 */
public class EntryMapMarker implements Serializable {
    private final String title;
    private final long id;
    private final EntryType entryType;
    private final double latitude;
    private final double longitude;

    public EntryMapMarker(String title, double latitude, double longitude, long id, EntryType entryType) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.entryType = entryType;
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

    public long getId() {
        return id;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryMapMarker mapMarker = (EntryMapMarker) o;

        if (Double.compare(mapMarker.latitude, latitude) != 0) return false;
        if (Double.compare(mapMarker.longitude, longitude) != 0) return false;
        if (mapMarker.id != id) return false;
        if (!mapMarker.entryType.equals(entryType)) return false;
        return title != null ? title.equals(mapMarker.title) : mapMarker.title == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = title.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + entryType.hashCode();
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

