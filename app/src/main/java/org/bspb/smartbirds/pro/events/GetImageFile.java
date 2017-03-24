package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 24.03.17.
 */

public class GetImageFile {
    public final String monitoringCode;
    public final String fileName;

    public GetImageFile(String monitoringCode, String fileName) {
        this.monitoringCode = monitoringCode;
        this.fileName = fileName;
    }
}
