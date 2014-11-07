package org.bspb.smartbirds.pro.events;

import android.location.Location;

/**
 * Created by dani on 14-11-6.
 */
public class LocationChangedEvent {

    public final Location location;

    public LocationChangedEvent(Location location) {
        this.location = location;
    }
}
