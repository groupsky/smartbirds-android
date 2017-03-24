package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 22.03.16.
 */
public class ImageFileCreatedFailed {

    public final String monitoringCode;

    public ImageFileCreatedFailed(String monitoringCode) {
        this.monitoringCode = monitoringCode;
    }
}
