package org.bspb.smartbirds.pro.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dani on 14-11-13.
 */
public class MapClickedEvent {

    public final LatLng position;

    public MapClickedEvent(LatLng position) {
        this.position = position;
    }

}
