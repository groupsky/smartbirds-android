package org.bspb.smartbirds.pro.events;

import android.net.Uri;

/**
 * Created by groupsky on 14-10-10.
 */
public class ImageFileCreated {

    public final String imageFileName;
    public final Uri uri;
    public final String imagePath;

    public ImageFileCreated(String imageFileName, Uri uri, String imagePath) {
        this.imageFileName = imageFileName;
        this.uri = uri;
        this.imagePath = imagePath;
    }
}
