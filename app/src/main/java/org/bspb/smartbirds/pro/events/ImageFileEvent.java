package org.bspb.smartbirds.pro.events;

import android.net.Uri;

/**
 * Created by groupsky on 24.03.17.
 */

public class ImageFileEvent {

    public final String monitoringCode;
    public final String imageFileName;
    public final Uri uri;
    public final String imagePath;

    public ImageFileEvent(String monitoringCode, String imageFileName, Uri uri, String imagePath) {
        this.monitoringCode = monitoringCode;
        this.imageFileName = imageFileName;
        this.uri = uri;
        this.imagePath = imagePath;
    }
}
