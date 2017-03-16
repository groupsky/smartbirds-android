package org.bspb.smartbirds.pro.events;

import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.HashMap;

/**
 * Created by groupsky on 14-9-29.
 */
public class EntrySubmitted {

    public long entryId;
    public HashMap<String, String> data;
    public EntryType entryType;
    public String monitoringCode;

    public EntrySubmitted(String monitoringCode, long entryId, HashMap<String, String> data, EntryType entryType) {
        this.monitoringCode = monitoringCode;
        this.entryId = entryId;
        this.data = data;
        this.entryType = entryType;
    }
}
