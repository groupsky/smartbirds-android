package org.bspb.smartbirds.pro.events;

/**
 * Created by groupsky on 14-9-29.
 */
public class UploadCompleted {
    public final long tag;

    public UploadCompleted(final long _tag) {
        tag = _tag;
    }
}
