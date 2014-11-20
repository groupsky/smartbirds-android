package org.bspb.smartbirds.pro.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by groupsky on 20.11.14.
 */
public class MapLongClickedEvent {

    public final LatLng position;

    public MapLongClickedEvent(LatLng position) {
        this.position = position;
    }

}
