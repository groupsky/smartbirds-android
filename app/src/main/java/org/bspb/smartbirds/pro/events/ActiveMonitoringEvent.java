package org.bspb.smartbirds.pro.events;

import org.bspb.smartbirds.pro.content.Monitoring;

/**
 * Created by groupsky on 20.03.17.
 */

public class ActiveMonitoringEvent {
    public Monitoring monitoring;

    public ActiveMonitoringEvent(Monitoring monitoring) {
        this.monitoring = monitoring;
    }
}
