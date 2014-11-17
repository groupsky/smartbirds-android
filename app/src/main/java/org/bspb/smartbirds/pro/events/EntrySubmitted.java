package org.bspb.smartbirds.pro.events;

import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.HashMap;

/**
 * Created by groupsky on 14-9-29.
 */
public class EntrySubmitted {

    public HashMap<String, String> data;
    public EntryType entryType;

    public EntrySubmitted(HashMap<String, String> data, EntryType entryType) {
        this.data = data;
        this.entryType = entryType;
    }
}
