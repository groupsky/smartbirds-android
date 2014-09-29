package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 14-9-29.
 */
public class UploadCompleted {
    final String monitoringPath;

    public UploadCompleted(String monitoringPath) {
        this.monitoringPath = monitoringPath;
    }
}
