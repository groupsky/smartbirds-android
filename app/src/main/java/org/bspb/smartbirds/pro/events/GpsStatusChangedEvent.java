package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 08.07.14.
 */
public class GpsStatusChangedEvent {
    public final boolean enabled;

    public GpsStatusChangedEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
