package org.bspb.smartbirds.pro.events;

import java.util.HashMap;

/**
 * Created by groupsky on 14-9-29.
 */
public class EntrySubmitted {

    public HashMap<String, String> data;

    public EntrySubmitted(HashMap<String, String> data) {
        this.data = data;
    }
}
