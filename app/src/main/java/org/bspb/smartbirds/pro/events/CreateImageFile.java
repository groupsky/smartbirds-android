package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 14-10-10.
 */
public class CreateImageFile {

    public final String monitoringCode;
    public final String action;

    public CreateImageFile(String monitoringCode, String action) {
        this.monitoringCode = monitoringCode;
        this.action = action;
    }
}
