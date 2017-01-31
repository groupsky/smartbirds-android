package org.bspb.smartbirds.pro.service;

import android.content.Context;
import android.content.Intent;

/**
 * Created by groupsky on 31.01.17.
 */
public class TrackingServiceBuilder extends TrackingService_.IntentBuilder_ {

    public TrackingServiceBuilder(Context context) {
        super(context);
    }

    public Intent getIntent() {
        return intent;
    }
}
