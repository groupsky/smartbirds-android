package org.bspb.smartbirds.pro.events;

import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.enums.EntryType;

/**
 * Created by groupsky on 14-9-25.
 */
public class StartMonitoringEvent {

    public final EntryType entryType;

    public StartMonitoringEvent(EntryType entryType) {
        this.entryType = entryType;
    }
}
