package org.bspb.smartbirds.pro.events;

import java.util.HashMap;

/**
 * Created by groupsky on 14-9-25.
 */
public class SetMonitoringCommonData {
    HashMap<String, String> data;

    public SetMonitoringCommonData(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
