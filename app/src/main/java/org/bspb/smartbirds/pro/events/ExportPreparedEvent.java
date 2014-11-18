package org.bspb.smartbirds.pro.events;

import android.net.Uri;

/**
 * Created by dani on 14-11-18.
 */
public class ExportPreparedEvent {

    public Uri uri;

    public ExportPreparedEvent(Uri uri) {
        this.uri = uri;
    }
}
